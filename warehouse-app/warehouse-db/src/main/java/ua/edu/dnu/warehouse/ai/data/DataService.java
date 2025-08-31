package ua.edu.dnu.warehouse.ai.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.edu.dnu.warehouse.ai.request.aggregation.AggregationField;
import ua.edu.dnu.warehouse.ai.request.aggregation.AggregationType;
import ua.edu.dnu.warehouse.ai.request.filter.FilterCondition;
import ua.edu.dnu.warehouse.ai.request.filter.FilterOperator;
import ua.edu.dnu.warehouse.ai.request.AiRequest;
import ua.edu.dnu.warehouse.ai.request.having.HavingCondition;
import ua.edu.dnu.warehouse.ai.request.order.OrderDirection;

import java.util.*;

@Service
public class DataService {
    private final EntityManager entityManager;

    public DataService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <E> Specification<E> buildSpecification(List<FilterCondition> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (FilterCondition filter : filters) {
                Predicate predicate = buildPredicate(root, cb, filter);
                predicates.add(predicate);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, FilterCondition filter){
        Path path = resolvePath(root, filter.fieldPath());
        return buildPredicate(path, cb, filter.operator(), filter.value());
    }

    private String likeValue(Object obj){
        String value = String.valueOf(obj).toLowerCase();
        if(value.contains("%")){
            return value;
        }
        return "%" + value + "%";
    }

    private Predicate buildPredicate(Expression ex, CriteriaBuilder cb, FilterOperator op, String val){
        Class<?> targetType = ex.getJavaType();
        Object value = castValue(val, targetType);
        return switch (op) {
            case EQUAL -> cb.equal(ex, value);
            case NOT_EQUAL -> cb.notEqual(ex, value);
            case LIKE -> cb.like(cb.lower(ex), likeValue(value));
            case GREATER_THAN -> cb.greaterThan(ex, (Comparable) value);
            case LESS_THAN -> cb.lessThan(ex, (Comparable) value);
        };
    }

    private Expression<?> buildExpression(Root<?> root, CriteriaBuilder cb, AggregationField aggregation){
        String field = aggregation.fieldPath();
        AggregationType type = aggregation.type();
        Path path = resolvePath(root, field);
        return switch (type) {
            case COUNT -> cb.count(path);
            case SUM   -> cb.sum(path);
            case AVG   -> cb.avg(path);
            case MIN   -> cb.min(path);
            case MAX   -> cb.max(path);
        };
    }

    private static Object castValue(String value, Class<?> targetType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            if (value == null) return null;
            if (targetType.isEnum()) {
                Class<? extends Enum> enumType = (Class<? extends Enum>) targetType;
                return Enum.valueOf(enumType, value.toUpperCase());
            }
            return mapper.readValue("\"" + value + "\"", targetType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot cast value: " + value + " to type: " + targetType.getSimpleName(), e);
        }
    }

    public List<Map<String, Object>> search(AiRequest request) {
        List<Tuple> tuples = executeSearch(request.entity().getType(), request);
        return convertTuplesToDto(tuples);
    }

    private <T> List<Tuple> executeSearch(
            Class<T> entityClass,
            AiRequest request
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<T> root = query.from(entityClass);

        Specification<T> spec = buildSpecification(request.filters());
        Predicate predicate = spec.toPredicate(root, query, cb);
        query.where(predicate);

        List<Selection<?>> selections = new ArrayList<>();
        boolean hasGroupBy = request.groupByFields() != null && !request.groupByFields().isEmpty();
        boolean hasAggregations = request.aggregations() != null && !request.aggregations().isEmpty();
        boolean hasHaving = hasGroupBy && request.havingConditions() != null && !request.havingConditions().isEmpty();
        boolean hasOrderBy = request.orderBy() != null && !request.orderBy().isEmpty();

        if (hasGroupBy) {
            Set<Path<?>> paths = new LinkedHashSet<>();
            for (String groupByField : request.groupByFields()) {
                Path<?> groupByPath = resolvePath(root, groupByField);
                selections.add(groupByPath.alias(groupByField));
                paths.add(groupByPath);
            }
            query.groupBy(paths.toArray(new Path[0]));
        }
        if (hasAggregations) {
            for (AggregationField aggregation : request.aggregations()) {
                Expression<?> expr = buildExpression(root, cb, aggregation);
                String alias = aggregation.type().name().toLowerCase() + "_" + aggregation.fieldPath();
                selections.add(expr.alias(alias));
            }
        }
        if (!hasGroupBy && !hasAggregations) {
            EntityType<T> entityType = entityManager.getMetamodel().entity(entityClass);
            for (Attribute<? super T, ?> attr : entityType.getAttributes()) {
                selections.add(root.get(attr.getName()).alias(attr.getName()));
            }
        }
        query.multiselect(selections);

        if (hasHaving) {
            List<Predicate> havingPredicates = new ArrayList<>();
            for (HavingCondition having : request.havingConditions()) {
                Expression<?> expr = buildExpression(root, cb, having.aggregation());
                Predicate havingPredicate = buildPredicate(expr, cb, having.operator(), having.value());
                havingPredicates.add(havingPredicate);
            }
            query.having(cb.and(havingPredicates.toArray(new Predicate[0])));
        }

        if (hasOrderBy && !hasGroupBy) {
            List<Order> orders = request.orderBy().stream()
                    .map(order -> {
                        Path<?> path = resolvePath(root, order.fieldPath());
                        return order.direction() == OrderDirection.DESC
                                ? cb.desc(path)
                                : cb.asc(path);
                    })
                    .toList();
            query.orderBy(orders);
        }

        return entityManager
                .createQuery(query)
                .setFirstResult(0)
                .setMaxResults(15)
                .getResultList();
    }

    private static List<Map<String, Object>> convertTuplesToDto(List<Tuple> tuples) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (TupleElement<?> element : tuple.getElements()) {
                String alias = element.getAlias();
                Object value = tuple.get(alias);
                map.put(alias, value);
            }
            results.add(map);
        }
        return results;
    }

    private static <T> Path resolvePath(Root<T> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        if (parts.length < 2){
            return root.get(parts[0]);
        }
        Join<?, ?> join = root.join(parts[0]);
        for (int i = 1; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }
        return join.get(parts[parts.length - 1]);
    }
}

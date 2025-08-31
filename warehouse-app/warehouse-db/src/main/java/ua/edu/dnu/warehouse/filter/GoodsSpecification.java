package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.field.GoodsField;
import ua.edu.dnu.warehouse.model.Goods;
import ua.edu.dnu.warehouse.model.GoodsStatus;
import ua.edu.dnu.warehouse.model.GoodsType;

import java.util.List;

public class GoodsSpecification implements SpecificationBuilder<Goods> {
    private GoodsType type;
    private GoodsStatus status;
    private String likeValue;
    private EntityField<?> orderBy;

    public GoodsSpecification withStatus(GoodsStatus status){
        this.status = status;
        return this;
    }

    public GoodsSpecification withType(GoodsType type){
        this.type = type;
        return this;
    }

    public GoodsSpecification containsValue(String likeValue){
        this.likeValue = likeValue;
        return this;
    }

    public GoodsSpecification sortedBy(EntityField<Goods> field){
        orderBy = field;
        return this;
    }

    @Override
    public Specification<Goods> build() {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (type != null) {
                Predicate typePredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        GoodsField.TYPE, type);
                predicate = criteriaBuilder.and(predicate, typePredicate);
            }
            if(status != null){
                Predicate statusPredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        GoodsField.STATUS, status);
                predicate = criteriaBuilder.and(predicate, statusPredicate);
            }
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likePredicateAll = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(GoodsField.NAME, GoodsField.DESCRIPTION), likeValue);
                predicate = criteriaBuilder.and(predicate, likePredicateAll);
            }
            if(orderBy != null){
                query.orderBy(criteriaBuilder.asc(root.get(orderBy.getName())));
            }
            return predicate;
        };
    }

}

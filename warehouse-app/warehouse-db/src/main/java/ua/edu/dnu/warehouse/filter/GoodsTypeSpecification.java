package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.field.GoodsTypeField;
import ua.edu.dnu.warehouse.model.GoodsType;

import java.util.List;

public class GoodsTypeSpecification implements SpecificationBuilder<GoodsType> {
    private String likeValue;
    private EntityField<?> orderBy;

    public GoodsTypeSpecification containsValue(String likeValue){
        this.likeValue = likeValue;
        return this;
    }

    public GoodsTypeSpecification sortedBy(EntityField<GoodsType> field){
        orderBy = field;
        return this;
    }

    @Override
    public Specification<GoodsType> build() {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likePredicateAll = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(GoodsTypeField.values()), likeValue);
                predicate = criteriaBuilder.and(predicate, likePredicateAll);
            }
            if(orderBy != null){
                query.orderBy(criteriaBuilder.asc(root.get(orderBy.getName())));
            }
            return predicate;
        };
    }

}

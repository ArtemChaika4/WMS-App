package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.field.CustomerField;
import ua.edu.dnu.warehouse.model.Customer;

import java.util.List;

public class CustomerSpecification implements SpecificationBuilder<Customer> {
    private String likeValue;
    private EntityField<?> orderBy;

    public CustomerSpecification containsValue(String likeValue){
        this.likeValue = likeValue;
        return this;
    }

    public CustomerSpecification sortedBy(EntityField<Customer> field){
        orderBy = field;
        return this;
    }

    public Specification<Customer> build(){
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likePredicateAll = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(CustomerField.values()), likeValue);
                predicate = criteriaBuilder.and(predicate, likePredicateAll);
            }
            if(orderBy != null) {
                query.orderBy(criteriaBuilder.asc(root.get(orderBy.getName())));
            }
            return predicate;
        };
    }

}

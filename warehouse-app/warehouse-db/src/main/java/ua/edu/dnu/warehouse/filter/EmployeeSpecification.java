package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.EmployeeField;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.model.*;

import java.util.List;

public class EmployeeSpecification implements SpecificationBuilder<Employee> {
    private Post post;
    private String likeValue;
    private EntityField<?> orderBy;

    public EmployeeSpecification withPost(Post post){
        this.post = post;
        return this;
    }

    public EmployeeSpecification containsValue(String likeValue){
        this.likeValue = likeValue;
        return this;
    }

    public EmployeeSpecification sortedBy(EntityField<Employee> field){
        orderBy = field;
        return this;
    }

    @Override
    public Specification<Employee> build() {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if(post != null){
                Predicate statusPredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        EmployeeField.POST, post);
                predicate = criteriaBuilder.and(predicate, statusPredicate);
            }
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likePredicateAll = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(EmployeeField.SURNAME, EmployeeField.NAME, EmployeeField.EMAIL), likeValue);
                predicate = criteriaBuilder.and(predicate, likePredicateAll);
            }
            if(orderBy != null){
                query.orderBy(criteriaBuilder.asc(root.get(orderBy.getName())));
            }
            return predicate;
        };
    }
}

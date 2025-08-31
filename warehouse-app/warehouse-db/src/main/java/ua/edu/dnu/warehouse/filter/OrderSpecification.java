package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.*;
import ua.edu.dnu.warehouse.model.*;

import java.util.List;

public class OrderSpecification implements SpecificationBuilder<Order> {
    private OrderStatus status;
    private String likeValue;
    private EntityField<?> orderBy;

    public OrderSpecification withStatus(OrderStatus status){
        this.status = status;
        return this;
    }

    public OrderSpecification containsValue(String likeValue){
        this.likeValue = likeValue;
        return this;
    }

    public <E> OrderSpecification sortedBy(EntityField<E> field){
        orderBy = field;
        return this;
    }

    @Override
    public Specification<Order> build() {
        return (root, query, criteriaBuilder) -> {
            Join<Order, Customer> join = root.join(OrderField.CUSTOMER.getName());
            Predicate predicate = criteriaBuilder.conjunction();
            if(status != null){
                Predicate statusPredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        OrderField.STATUS, status);
                predicate = criteriaBuilder.and(predicate, statusPredicate);
            }
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likeOrder = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(), likeValue);
                Predicate likeCustomer = PredicateBuilder.likeAll(criteriaBuilder, join,
                        List.of(CustomerField.values()), likeValue);
                Predicate like = criteriaBuilder.or(likeOrder, likeCustomer);
                predicate = criteriaBuilder.and(predicate, like);
            }
            if(orderBy != null){
                String name = orderBy.getName();
                Expression<?> exp;
                if(orderBy.getEntityClass() == Order.class) {
                    exp = root.get(name);
                }else {
                    exp = join.get(name);
                }
                query.orderBy(criteriaBuilder.asc(exp));
            }
            return predicate;
        };
    }
}

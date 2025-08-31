package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.field.EmployeeField;
import ua.edu.dnu.warehouse.field.EventLogField;
import ua.edu.dnu.warehouse.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class EventLogSpecification implements SpecificationBuilder<EventLog> {
    private String likeValue;
    private LocalDateTime afterDate;
    private LocalDateTime beforeDate;
    private Category category;
    private Action action;
    private Post post;

    public EventLogSpecification withCategory(Category category){
        this.category = category;
        return this;
    }

    public EventLogSpecification withAction(Action action){
        this.action = action;
        return this;
    }

    public EventLogSpecification withPost(Post post){
        this.post = post;
        return this;
    }

    public EventLogSpecification containsValue(String value){
        likeValue = value;
        return this;
    }

    public EventLogSpecification withAfterDate(LocalDateTime date){
        afterDate = date;
        return this;
    }

    public EventLogSpecification withBeforeDate(LocalDateTime date){
        beforeDate = date;
        return this;
    }

    @Override
    public Specification<EventLog> build() {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            Join<EventLog, Employee> employeeJoin = root.join(EventLogField.EMPLOYEE.getName(), JoinType.LEFT);
            if(category != null){
                Predicate categoryPredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        EventLogField.CATEGORY, category);
                predicate = criteriaBuilder.and(predicate, categoryPredicate);
            }
            if(action != null){
                Predicate actionPredicate = PredicateBuilder.equal(criteriaBuilder, root,
                        EventLogField.ACTION, action);
                predicate = criteriaBuilder.and(predicate, actionPredicate);
            }
            if(post != null){
                Predicate postPredicate = PredicateBuilder.equal(criteriaBuilder, employeeJoin,
                        EmployeeField.POST, post);
                predicate = criteriaBuilder.and(predicate, postPredicate);
            }
            if (likeValue != null && !likeValue.isEmpty()) {
                Predicate likeAllLog = PredicateBuilder.likeAll(criteriaBuilder, root,
                        List.of(EventLogField.RESULT), likeValue);
                Predicate likeAllEmployee = PredicateBuilder.likeAll(criteriaBuilder, employeeJoin,
                        List.of(EmployeeField.NAME, EmployeeField.SURNAME, EmployeeField.EMAIL), likeValue);
                Predicate likeAll = criteriaBuilder.or(likeAllLog, likeAllEmployee);
                predicate = criteriaBuilder.and(predicate, likeAll);
            }
            if(afterDate != null){
                Predicate afterDatePredicate = PredicateBuilder.greaterThanOrEqual(criteriaBuilder, root,
                        EventLogField.TIMESTAMP, afterDate);
                predicate = criteriaBuilder.and(predicate, afterDatePredicate);
            }
            if(beforeDate != null){
                Predicate beforeDatePredicate = PredicateBuilder.lessThanOrEqual(criteriaBuilder, root,
                        EventLogField.TIMESTAMP, beforeDate);
                predicate = criteriaBuilder.and(predicate, beforeDatePredicate);
            }
            query.orderBy(criteriaBuilder.asc(root.get(EventLogField.TIMESTAMP.getName())));
            return predicate;
        };
    }
}

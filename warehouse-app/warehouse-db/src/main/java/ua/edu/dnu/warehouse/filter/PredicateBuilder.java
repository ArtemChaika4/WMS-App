package ua.edu.dnu.warehouse.filter;

import jakarta.persistence.criteria.*;
import ua.edu.dnu.warehouse.field.EmployeeField;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.model.EventLog;
import ua.edu.dnu.warehouse.model.Post;

import java.util.List;


public class PredicateBuilder {
    public static <T> Predicate like(CriteriaBuilder cb, Root<T> root,
                                     EntityField<T> field, String value) {
        return like(cb, root.get(field.getName()), value);
    }

    private static Predicate like(CriteriaBuilder cb, Expression<String> expression, String value){
        return cb.like(cb.lower(expression), "%" + value.toLowerCase() + "%");
    }
    public static <T, E> Predicate like(CriteriaBuilder cb, Join<T, E> join,
                                     EntityField<E> field, String value) {
        return like(cb, join.get(field.getName()), value);
    }

    public static <T> Predicate likeAll(CriteriaBuilder criteriaBuilder, Root<T> root,
                                        List<EntityField<T>> fields, String value) {
        return criteriaBuilder.or(fields.stream()
                .map(field -> like(criteriaBuilder, root, field, value))
                .toArray(Predicate[]::new));
    }

    public static <T, E> Predicate likeAll(CriteriaBuilder criteriaBuilder, Join<T, E> join,
                                        List<EntityField<E>> fields, String value) {
        return criteriaBuilder.or(fields.stream()
                .map(field -> like(criteriaBuilder, join, field, value))
                .toArray(Predicate[]::new));
    }

    public static <T> Predicate equal(CriteriaBuilder criteriaBuilder, Root<T> root,
                                      EntityField<T> field, Object value) {
        return criteriaBuilder.equal(root.get(field.getName()), value);
    }

    public static <T> Predicate greaterThanOrEqual(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                   EntityField<T> field, Comparable value) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get(field.getName()), value);
    }

    public static <T> Predicate lessThanOrEqual(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                   EntityField<T> field, Comparable value) {
        return criteriaBuilder.lessThanOrEqualTo(root.get(field.getName()), value);
    }

    public static <T> Predicate betweenPredicate(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                 EntityField<T> field, Comparable start, Comparable end) {
        return criteriaBuilder.between(root.get(field.getName()), start, end);
    }

    public static <T, E> Predicate equal(CriteriaBuilder criteriaBuilder, Join<T, E> join,
                                  EntityField<E> field, Object value) {
        return criteriaBuilder.equal(join.get(field.getName()), value);
    }
}

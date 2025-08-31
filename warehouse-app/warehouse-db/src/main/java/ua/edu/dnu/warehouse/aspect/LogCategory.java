package ua.edu.dnu.warehouse.aspect;

import ua.edu.dnu.warehouse.model.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogCategory {
    Category value();
}

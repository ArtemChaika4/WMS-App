package ua.edu.dnu.warehouse.aspect;

import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Category;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAction {
    Action value();
    String message() default "";
}

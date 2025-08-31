package ua.edu.dnu.warehouse.field;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public interface EntityField<E> {
    String getName();
    String getLabel();
    Class<E> getEntityClass();

    default String getAttribute(){
        Column column = getAnnotation(Column.class);
        JoinColumn joinColumn = getAnnotation(JoinColumn.class);
        if(column != null && !column.name().isEmpty()){
            return column.name();
        }
        if(joinColumn != null && !joinColumn.name().isEmpty()){
            return joinColumn.name();
        }
        return getName();
    }

    default Field getField(){
        try {
            return getEntityClass().getDeclaredField(getName());
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    default Class<?> getType(){
        return getField().getType();
    }

    default <T extends Annotation> T getAnnotation(Class<T> a){
        return getField().getAnnotation(a);
    }

}

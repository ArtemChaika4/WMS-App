package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.OrderLog;

public enum OrderLogField implements EntityField<OrderLog> {
    ID("id", "ID"),
    ORDER("order", "Замовлення"),
    STATUS("status", "Оновлений статус замовлення"),
    TIMESTAMP("timestamp", "Дата та час"),
    EMPLOYEE("employee", "Працівник");

    private final String name;
    private final String label;

    OrderLogField(String name, String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Class<OrderLog> getEntityClass(){
        return OrderLog.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

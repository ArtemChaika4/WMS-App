package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.OrderItem;

public enum OrderItemField implements EntityField<OrderItem> {
    ID("id", "ID"),
    ORDER("order", "Замовлення"),
    GOODS("goods", "Товари замовлення"),
    NUMBER("number", "Кільксть одиниць товару"),
    PRICE("price", "Ціна за одиницю");

    private final String name;
    private final String label;

    OrderItemField(String name, String label) {
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
    public Class<OrderItem> getEntityClass(){
        return OrderItem.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

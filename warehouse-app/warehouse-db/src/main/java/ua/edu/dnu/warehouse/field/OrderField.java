package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.Order;

public enum OrderField implements EntityField<Order>{
    ID("id", "ID"),
    CUSTOMER("customer", "Клієнт"),
    DATE("date", "Дата замовлення"),
    TOTAL("total", "Ціна замовлення"),
    STATUS("status", "Статус замовлення");

    private final String name;
    private final String label;

    OrderField(String name, String label) {
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
    public Class<Order> getEntityClass(){
        return Order.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

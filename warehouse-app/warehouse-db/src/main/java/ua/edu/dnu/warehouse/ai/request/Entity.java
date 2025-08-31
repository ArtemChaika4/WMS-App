package ua.edu.dnu.warehouse.ai.request;

import ua.edu.dnu.warehouse.model.*;

public enum Entity {
    ORDER(Order.class),
    GOODS(Goods.class),
    GOODS_TYPE(GoodsType.class),
    ORDER_ITEM(OrderItem.class),
    ORDER_LOG(OrderLog.class),
    CUSTOMER(Customer.class),
    EMPLOYEE(Employee.class),
    EVENT_LOG(EventLog.class);

    private final Class<?> type;

    Entity(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
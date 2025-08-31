package ua.edu.dnu.warehouse.model;

public enum Category {
    ORDERS("Замовлення"),
    CUSTOMERS("Замовники"),
    GOODS("Товари"),
    GOODS_TYPES("Види товарів"),
    EMPLOYEES("Працівники");


    private final String label;


    public String getLabel() {
        return label;
    }

    Category(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

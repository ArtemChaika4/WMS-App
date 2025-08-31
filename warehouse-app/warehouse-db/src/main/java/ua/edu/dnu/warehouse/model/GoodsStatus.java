package ua.edu.dnu.warehouse.model;

public enum GoodsStatus {
    AVAILABLE("У наявності"),
    EXPECTED("Очікується"),
    DELETED("Видалено");

    private final String label;

    public String getLabel() {
        return label;
    }

    GoodsStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

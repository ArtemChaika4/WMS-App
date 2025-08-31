package ua.edu.dnu.warehouse.model;

public enum OrderStatus {
    CREATED("Нове замовлення", false),
    PROCESSED("Опрацьовано", false),
    READY("Готове до видачі", false),
    COMPLETED("Виконано", false),
    CANCELED("Скасовано", true),
    RETURNED("Повернуто", true);

    private final boolean canceled;
    private final String label;

    OrderStatus(String label, boolean canceled) {
        this.label = label;
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }


    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

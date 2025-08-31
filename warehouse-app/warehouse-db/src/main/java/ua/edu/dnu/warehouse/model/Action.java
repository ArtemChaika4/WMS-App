package ua.edu.dnu.warehouse.model;

public enum Action {
    CREATE("Створення"),
    UPDATE("Оновлення"),
    DELETE("Видалення"),
    LOGIN("Вхід в систему");

    private final String label;


    public String getLabel() {
        return label;
    }

    Action(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

package ua.edu.dnu.warehouse.model;

public enum Post {
    ADMINISTRATOR("Адміністратор"),
    WAREHOUSEMAN("Працівник складу"),
    OPERATOR("Оператор");

    private final String label;

    Post(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

}
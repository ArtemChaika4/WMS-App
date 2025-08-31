package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.EventLog;

public enum EventLogField implements EntityField<EventLog> {
    ID("id", "ID"),
    RESULT("result", "Результат"),
    ACTION("action", "Дія"),
    CATEGORY("category", "Категорія"),
    TIMESTAMP("timestamp", "Дата та час"),
    EMPLOYEE("employee", "Працівник");

    private final String name;
    private final String label;

    EventLogField(String name, String label) {
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
    public Class<EventLog> getEntityClass(){
        return EventLog.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.Employee;

public enum EmployeeField implements EntityField<Employee> {
    ID("id", "ID"),
    NAME("name", "Ім'я"),
    SURNAME("surname", "Прізвище"),
    PATRONYMIC("patronymic", "По батькові"),
    EMAIL("email", "Електронна адреса"),
    POST("post", "Посада");

    private final String name;
    private final String label;

    EmployeeField(String name, String label) {
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
    public Class<Employee> getEntityClass(){
        return Employee.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

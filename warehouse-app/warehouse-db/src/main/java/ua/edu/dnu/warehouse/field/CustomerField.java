package ua.edu.dnu.warehouse.field;


import ua.edu.dnu.warehouse.model.Customer;

public enum CustomerField implements EntityField<Customer> {
    ID("id", "ID"),
    NAME("name", "Ім'я"),
    SURNAME("surname", "Прізвище"),
    PATRONYMIC("patronymic", "По батькові"),
    PHONE("phone", "Номер телефону"),
    ADDRESS("address", "Адреса");

    private final String name;
    private final String label;

    CustomerField(String name, String label) {
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
    public Class<Customer> getEntityClass(){
        return Customer.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

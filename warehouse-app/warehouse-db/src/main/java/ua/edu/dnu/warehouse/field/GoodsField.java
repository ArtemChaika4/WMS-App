package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.Goods;

public enum GoodsField implements EntityField<Goods> {
    ID("id", "ID"),
    NAME("name", "Назва товару"),
    DESCRIPTION("description", "Опис товару"),
    AMOUNT("amount", "Кількість одиниць"),
    PRICE("price", "Ціна за одиницю"),
    TYPE("type", "Тип товару"),
    STATUS("status", "Статус товару");

    private final String name;
    private final String label;

    GoodsField(String name, String label) {
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
    public Class<Goods> getEntityClass(){
        return Goods.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

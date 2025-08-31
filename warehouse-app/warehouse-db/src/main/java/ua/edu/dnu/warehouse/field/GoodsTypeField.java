package ua.edu.dnu.warehouse.field;

import ua.edu.dnu.warehouse.model.GoodsType;

public enum GoodsTypeField implements EntityField<GoodsType> {
    ID("id", "ID"),
    NAME("name", "Назва типу"),
    DESCRIPTION("description", "Опис типу");

    private final String name;
    private final String label;

    GoodsTypeField(String name, String label) {
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
    public Class<GoodsType> getEntityClass(){
        return GoodsType.class;
    }

    @Override
    public String toString() {
        return label;
    }
}

package ua.edu.dnu.warehouse.ai.schema;

import ua.edu.dnu.warehouse.ai.request.Entity;

import java.util.List;

public record FieldSchema(
        String name,
        String description,
        String type,
        boolean isEntity,
        boolean isEnum,
        List<EnumValue> enumValue,
        Entity refEntity
) {}

package ua.edu.dnu.warehouse.ai.schema;

import ua.edu.dnu.warehouse.ai.request.Entity;

import java.util.List;

public record EntitySchema(
        Entity entity,
        List<FieldSchema> fields
) {}

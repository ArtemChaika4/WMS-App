package ua.edu.dnu.warehouse.ai.request.aggregation;

public record AggregationField(
        String fieldPath,
        AggregationType type
) {}

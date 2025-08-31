package ua.edu.dnu.warehouse.ai.request.filter;


public record FilterCondition(
        String fieldPath,
        FilterOperator operator,
        String value
) {}
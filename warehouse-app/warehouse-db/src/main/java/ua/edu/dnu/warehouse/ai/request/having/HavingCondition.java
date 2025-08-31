package ua.edu.dnu.warehouse.ai.request.having;

import ua.edu.dnu.warehouse.ai.request.aggregation.AggregationField;
import ua.edu.dnu.warehouse.ai.request.filter.FilterOperator;

public record HavingCondition (
        AggregationField aggregation,
        FilterOperator operator,
        String value
)
{}

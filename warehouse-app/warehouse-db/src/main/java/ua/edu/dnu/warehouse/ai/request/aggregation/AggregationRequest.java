package ua.edu.dnu.warehouse.ai.request.aggregation;

import ua.edu.dnu.warehouse.ai.request.filter.FilterCondition;

import java.util.List;

public record AggregationRequest(
        List<FilterCondition> filters,
        String groupByField,
        List<AggregationField> aggregations
) {}

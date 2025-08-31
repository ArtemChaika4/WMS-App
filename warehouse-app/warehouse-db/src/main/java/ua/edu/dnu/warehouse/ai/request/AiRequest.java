package ua.edu.dnu.warehouse.ai.request;

import ua.edu.dnu.warehouse.ai.request.aggregation.AggregationField;
import ua.edu.dnu.warehouse.ai.request.filter.FilterCondition;
import ua.edu.dnu.warehouse.ai.request.having.HavingCondition;
import ua.edu.dnu.warehouse.ai.request.order.OrderBy;

import java.util.List;

public record AiRequest (
        Entity entity,
        List<FilterCondition> filters,
        List<AggregationField> aggregations,
        List<String> groupByFields,
        List<HavingCondition> havingConditions,
        List<OrderBy> orderBy
)
{}

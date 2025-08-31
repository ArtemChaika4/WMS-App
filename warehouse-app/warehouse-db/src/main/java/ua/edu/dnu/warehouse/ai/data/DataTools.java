package ua.edu.dnu.warehouse.ai.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.ai.json.JsonFormatter;
import ua.edu.dnu.warehouse.ai.request.aggregation.AggregationField;
import ua.edu.dnu.warehouse.ai.request.filter.FilterCondition;
import ua.edu.dnu.warehouse.ai.request.*;
import ua.edu.dnu.warehouse.ai.request.Entity;
import ua.edu.dnu.warehouse.ai.request.having.HavingCondition;
import ua.edu.dnu.warehouse.ai.request.order.OrderBy;

import java.util.List;
import java.util.Map;

@Component
public class DataTools {
    private final DataService dataService;

    public DataTools(DataService dataService) {
        this.dataService = dataService;
    }

    public void test(){
        String json = """
                {
                  "entity" : "ORDER",
                  "filters" : [ ],
                  "aggregations" : [ {
                    "fieldPath" : "id",
                    "type" : "COUNT"
                  }, {
                    "fieldPath" : "total",
                    "type" : "SUM"
                  } ],
                  "groupByFields" : [ "customer.id", "customer.name", "customer.surname" ],
                  "havingConditions" : [ {
                    "aggregation" : {
                      "fieldPath" : "id",
                      "type" : "COUNT"
                    },
                    "operator" : "GREATER_THAN",
                    "value" : "2"
                  }, {
                    "aggregation" : {
                      "fieldPath" : "total",
                      "type" : "SUM"
                    },
                    "operator" : "GREATER_THAN",
                    "value" : "30000"
                  } ],
                  "orderBy" : [ {
                    "fieldPath" : "total",
                    "direction" : "DESC"
                  } ]
                }    
                """;
        AiRequest request = JsonFormatter.convertToObject(json, AiRequest.class);
        JsonFormatter.printAsJson(request);
        System.out.println(dataService.search(request));
    }

    @Tool(
            name = "searchEntities",
            description = """
        Search and aggregate entities using:
        - Filter conditions (filters)
        - Aggregation functions (aggregations: COUNT, SUM, AVG, MIN, MAX)
        - Grouping by specific fields (groupByFields)
        - HAVING conditions on aggregated values applied to groups (havingConditions)
        - Result ordering (orderByFields)
        Supported operators: EQUAL, NOT_EQUAL, LIKE, GREATER_THAN, LESS_THAN.
        Supported order directions: ASC, DESC.
        All parameters are required. If some parameters are not needed, they must be left empty [].
    """
    )
    public List<Map<String, Object>> searchEntities(
            @ToolParam(description = "Entity type to search") Entity entity,
            @ToolParam(description = "Filtering conditions for entity fields") List<FilterCondition> filters,
            @ToolParam(description = "Fields and aggregation types (e.g., SUM, COUNT)") List<AggregationField> aggregations,
            @ToolParam(description = "Fields to group results by") List<String> groupByFields,
            @ToolParam(description = "Conditions applied to aggregated values (HAVING)") List<HavingCondition> havingConditions,
            @ToolParam(description = "Fields to order the result set by") List<OrderBy> orderByFields
    ) {
        AiRequest request = new AiRequest(
                entity,
                getFilters(filters),
                getAggregations(aggregations),
                groupByFields,
                getHavingConditions(havingConditions),
                getOrderByFields(orderByFields)
        );
        JsonFormatter.printAsJson(request);
        return dataService.search(request);
    }

    private List<FilterCondition> getFilters(List<?> filters){
        ObjectMapper mapper = new ObjectMapper();
        return filters.stream()
                .map(data -> mapper.convertValue(data, FilterCondition.class))
                .toList();
    }

    private List<AggregationField> getAggregations(List<?> aggregations){
        ObjectMapper mapper = new ObjectMapper();
        return aggregations.stream()
                .map(data -> mapper.convertValue(data, AggregationField.class))
                .toList();
    }

    private List<HavingCondition> getHavingConditions(List<?> havingConditions){
        ObjectMapper mapper = new ObjectMapper();
        return havingConditions.stream()
                .map(data -> mapper.convertValue(data, HavingCondition.class))
                .toList();
    }

    private List<OrderBy> getOrderByFields(List<?> orderByFields){
        ObjectMapper mapper = new ObjectMapper();
        return orderByFields.stream()
                .map(data -> mapper.convertValue(data, OrderBy.class))
                .toList();
    }
}

package ua.edu.dnu.warehouse.ai.request.order;

public record OrderBy(
        String fieldPath,
        OrderDirection direction
) {
}

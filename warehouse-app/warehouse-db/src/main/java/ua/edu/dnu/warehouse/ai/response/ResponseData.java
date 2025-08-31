package ua.edu.dnu.warehouse.ai.response;

import java.util.List;
import java.util.Map;

public record ResponseData(
        List<String> columns,                // Назви колонок (для таблиці або підписів на графіку)
        List<Map<String, Object>> rows       // Кожен рядок — це об'єкт зі значеннями по назвах колонок

) {
}

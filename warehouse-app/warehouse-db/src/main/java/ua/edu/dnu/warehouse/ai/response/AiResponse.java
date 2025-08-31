package ua.edu.dnu.warehouse.ai.response;

public record AiResponse (
        String message,
        ResponseData data,
        ResponseType type
) {
}

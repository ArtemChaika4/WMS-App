package ua.edu.dnu.warehouse.ai.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFormatter {

    public static <T> T convertToObject(String json, Class<T> type)  {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            obj = mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String convertToJson(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static void printAsJson(Object obj) {
        System.out.println(convertToJson(obj));
    }
}
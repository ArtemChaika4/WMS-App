package ua.edu.dnu.warehouse.ai.schema;

import ua.edu.dnu.warehouse.ai.request.Entity;
import ua.edu.dnu.warehouse.field.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EntitySchemaBuilder {
    private static final Map<Entity, EntityField<?>[]> entities =
            Map.of(Entity.CUSTOMER, CustomerField.values(),
                    Entity.GOODS, GoodsField.values(),
                    Entity.GOODS_TYPE, GoodsTypeField.values(),
                    Entity.ORDER, OrderField.values(),
                    Entity.ORDER_ITEM, OrderItemField.values(),
                    Entity.ORDER_LOG, OrderLogField.values(),
                    Entity.EMPLOYEE, EmployeeField.values(),
                    Entity.EVENT_LOG, EventLogField.values());


    private static Entity getEntityByType(Class<?> type){
        return Arrays.stream(Entity.values())
                .filter(entity -> entity.getType().equals(type))
                .findFirst().orElse(null);
    }

    public static EntitySchema getEntitySchema(Entity entity){
        EntityField<?>[] entityFields = entities.get(entity);
        if(entityFields == null){
            return null;
        }
        List<FieldSchema> fieldSchemas = new ArrayList<>();
        for (EntityField<?> field : entityFields) {
            Entity nestedEntity = getEntityByType(field.getType());
            boolean isEntity = nestedEntity != null;
            boolean isEnum = field.getType().isEnum();
            List<EnumValue> enumValues = isEnum
                    ? Arrays.stream(((Class<? extends Enum<?>>) field.getType()).getEnumConstants())
                    .map(e -> new EnumValue(e.name(), e.toString()))
                    .toList()
                    : null;
            fieldSchemas.add(new FieldSchema(
                    field.getName(),
                    field.getLabel(),
                    field.getType().getSimpleName(),
                    isEntity,
                    isEnum,
                    enumValues,
                    nestedEntity
            ));
        }
        return new EntitySchema(entity, fieldSchemas);
    }


    public static List<EntitySchema> getAllEntitySchemas(){
        return entities.keySet().stream().
                map(EntitySchemaBuilder::getEntitySchema)
                .toList();
    }
}

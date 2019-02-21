package gg.manny.brawl.entity.type;

import java.lang.reflect.Field;

public class CustomEntity {

    private Object getField(String fieldName, Class clazz, Object object) {
        Field field;
        Object newObject = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            newObject = field.get(object);
        }
        catch(NoSuchFieldException | IllegalAccessException ignored) { }
        return newObject;
    }

}

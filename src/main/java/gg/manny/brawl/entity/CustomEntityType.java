package gg.manny.brawl.entity;

import gg.manny.brawl.entity.type.CustomEntitySilverfish;
import net.minecraft.server.v1_7_R4.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

import java.lang.reflect.Field;
import java.util.Map;


public enum CustomEntityType {

    CUSTOM_SILVERFISH("Silverfish", 60, CustomEntitySilverfish.class);

    private final String name;
    private final int id;
    private final Class<? extends Entity> clazz;

    CustomEntityType(String name, int id, Class<? extends Entity> clazz) {
        this.name = name;
        this.id = id;
        this.clazz = clazz;

        ((Map)getField("c", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, clazz);
        ((Map)getField("d", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, name);
        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
        ((Map)getField("f", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
    }

    public static void spawn(Entity entity, Location location) {
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        ((CraftWorld)location.getWorld()).getHandle().addEntity(entity);
    }

    private static Object getField(String fieldName, Class clazz, Object object) {
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

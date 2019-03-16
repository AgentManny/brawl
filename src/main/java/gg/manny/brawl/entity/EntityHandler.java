package gg.manny.brawl.entity;

import gg.manny.brawl.entity.type.CustomMonsterEntity;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class EntityHandler {

    private static Map<CustomEntityType, Class<? extends CustomMonsterEntity>> entityMap = new HashMap<>();

    private EntityHandler() {

    }


    public static void get(CustomEntityType entityType, Location location) {
        try {
            CustomMonsterEntity entity = entityMap.get(entityType).newInstance();
            entity.spawn(location);
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
    }

    public enum CustomEntityType {

        SILVERFISH

    }

}

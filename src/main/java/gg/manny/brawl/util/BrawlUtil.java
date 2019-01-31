package gg.manny.brawl.util;

import com.google.common.reflect.TypeToken;
import gg.manny.brawl.Brawl;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrawlUtil {

    public static Type MAP_INTEGER_STRING = new TypeToken<Map<Integer, String>>(){}.getType();

    public static ItemStack[] convert(Material... materials) {
        List<ItemStack> items = new ArrayList<>();
        for(Material material : materials) {
            items.add(new ItemStack(material));
        }
        return items.toArray(new ItemStack[]{});
    }

    public static boolean has(Document document, String key) {
        return document != null && document.containsKey(key) && document.get("key") != null;
    }

    public static void registerCommand(Command command) {
        Field bukkitCommandMap = null;
        try {
            bukkitCommandMap = Brawl.getInstance().getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        CommandMap commandMap;
        try {
            commandMap = (CommandMap) bukkitCommandMap.get(Brawl.getInstance().getServer());
            command.setLabel(Brawl.getInstance().getDescription().getName() + ':' + command.getName());
            commandMap.register(Brawl.getInstance().getDescription().getName(), command);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

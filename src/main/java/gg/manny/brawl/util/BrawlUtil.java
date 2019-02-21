package gg.manny.brawl.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.stream.Location;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BrawlUtil {

    public final static Type MAP_INTEGER_STRING = new TypeToken<Map<Integer, String>>(){}.getType();
    public final static Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    public static ItemStack[] convert(Material... materials) {
        List<ItemStack> items = new ArrayList<>();
        for(Material material : materials) {
            items.add(new ItemStack(material));
        }
        return items.toArray(new ItemStack[]{});
    }

    public static ItemStack create(Material material) {
        return new ItemStack(material, 1);
    }

    public static boolean match(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return false;
        }
        return item1.getType() == item2.getType() && (item1.hasItemMeta() && item1.getItemMeta().hasDisplayName() && item2.hasItemMeta() && item2.getItemMeta().hasDisplayName() && item1.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName()));
    }


    public static List<Player> getNearbyPlayers(Player player, double radius) {
        return player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).map(entity -> ((Player)entity)).collect(Collectors.toList());
    }

    public static boolean has(JsonObject jsonObject, String key) {
        return jsonObject != null && !jsonObject.isJsonNull() && jsonObject.has(key) && jsonObject.get(key) != null && !jsonObject.isJsonNull();
    }

    public static boolean isJson(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGeneric() {
        try {
            Class.forName("gg.manny.spigot.GenericSpigot");
        } catch (ClassNotFoundException ignored) {
            return false;
        }
        return true;
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

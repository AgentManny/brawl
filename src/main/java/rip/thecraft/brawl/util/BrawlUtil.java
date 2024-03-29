package rip.thecraft.brawl.util;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.server.CraftServer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BrawlUtil {

    public final static Type MAP_INTEGER_STRING = new TypeToken<Map<Integer, String>>(){}.getType();
    public final static Type MAP_STRING_LOCATION = new TypeToken<Map<String, Location>>(){}.getType();

    public final static Type TREE_STRING_LOCATION = new TypeToken<TreeMap<String, Location>>(){}.getType();

    public final static Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    public static boolean isHidePlayersFromTab() {
        return CraftServer.getInstance().getConfig().isHidePlayersFromTab();
    }

    public static String getProgressBar(int current, int maxValue, char symbol, int bars) {
        float percent = (float) current / maxValue;
        int progressBars = (int) (bars * percent);

        return Strings.repeat("" + ChatColor.LIGHT_PURPLE + symbol, progressBars)
                + Strings.repeat("" + ChatColor.GRAY + symbol, bars - progressBars);
    }
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



    public static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static boolean isUUID(String string) {
        return UUID_PATTERN.matcher(string).find();
    }

    public static List<Player> getNearbyPlayers(Entity player, double radius) {
        return player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).map(entity -> ((Player)entity)).collect(Collectors.toList());
    }

    public static boolean has(JsonObject jsonObject, String key) {
        return jsonObject != null && !jsonObject.isJsonNull() && jsonObject.has(key) &&
                jsonObject.get(key) != null && !jsonObject.get(key).isJsonNull() && jsonObject.has(key) && jsonObject.get(key) != null;
    }


    public static boolean isMineSpigot() {
        try {
            Class.forName("rip.thecraft.server.CraftServer");
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

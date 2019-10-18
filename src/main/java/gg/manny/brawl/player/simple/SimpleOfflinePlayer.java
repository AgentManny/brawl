package gg.manny.brawl.player.simple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class SimpleOfflinePlayer {

    @Getter
    private static Set<SimpleOfflinePlayer> offlinePlayers = new HashSet<>();

    private final UUID uuid;
    private String name;
    private int kills = 0;
    private int deaths = 0;

    public SimpleOfflinePlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;

        offlinePlayers.add(this);
    }

    public SimpleOfflinePlayer(Player player) {
        this(player.getName(), player.getUniqueId());
    }

    public Player getPlayer() {
        return Brawl.getInstance().getServer().getPlayer(uuid);
    }

    public PlayerData getPlayerData() {
        return Brawl.getInstance().getPlayerDataHandler().getPlayerData(this.uuid);
    }

    public void addKills() {
        this.kills++;
    }

    public void addDeaths() {
        this.deaths++;
    }

    public static void init(Player player) {
        SimpleOfflinePlayer simplePlayer;
        if ((simplePlayer = getByUuid(player.getUniqueId())) == null) {
            new SimpleOfflinePlayer(player);
        } else {
            if (!(simplePlayer.getName().equals(player.getName()))) {
                simplePlayer.setName(player.getName());
            }
        }
    }

    public static void save(JavaPlugin main) throws IOException {
        if (!(offlinePlayers.isEmpty())) {
            File file = new File(main.getDataFolder(), "offlineplayers.json");

            Writer writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(offlinePlayers);
            writer.write(json);
            writer.close();
        }
    }
    public static void load(JavaPlugin main) {
        File file = new File(main.getDataFolder(), "offlineplayers.json");

        if (file.exists()) {
            Gson gson = new Gson();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Type type = new TypeToken<Set<SimpleOfflinePlayer>>(){}.getType();
                Set<SimpleOfflinePlayer> set = gson.fromJson(reader, type);
                if (set != null) {
                    offlinePlayers.addAll(set);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

         for (Player player : main.getServer().getOnlinePlayers()) {
             if (getByUuid(player.getUniqueId()) == null) {
                 new SimpleOfflinePlayer(player);
             }
         }
    }

    public static SimpleOfflinePlayer getByUuid(UUID uuid) {
        for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if (offlinePlayer.getUuid().equals(uuid)) {
                return offlinePlayer;
            }
        }
        return null;
    }

    public static UUID getUuidByName(String name) {
        for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer.getUuid();
            }
        }
        return null;
    }

    public static String getNameByUuid(UUID uuid) {
        for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if (offlinePlayer.getUuid().equals(uuid)) {
                return offlinePlayer.getName();
            }
        }
        return null;
    }

    public static SimpleOfflinePlayer getByName(String name) {
        for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }
        return null;
    }

}
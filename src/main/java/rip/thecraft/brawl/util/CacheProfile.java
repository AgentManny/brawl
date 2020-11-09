package rip.thecraft.brawl.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.util.UUIDs;
import rip.thecraft.spartan.uuid.MUUIDCache;

import java.util.UUID;

// Profile used
public class CacheProfile {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final JsonParser JSON_PARSER = new JsonParser();

    @Getter private UUID uuid;
    @Getter private final String username;


    private boolean exists = false;

    public CacheProfile(String name) throws Exception {
        Player player;
        if ((player = Bukkit.getPlayer(name)) != null) {
            this.uuid = player.getUniqueId();
            this.username = player.getName();
            exists = true;
            return;
        }
        if ((this.uuid = MUUIDCache.uuid(name)) == null) {
            Request request = new Request.Builder()
                    .url("https://api.mojang.com/users/profiles/minecraft/" + name)
                    .build();
            Response response = CLIENT.newCall(request).execute();
            if (response.isSuccessful() && response.code() == 200) {
                JsonElement element = JSON_PARSER.parse(response.body().string());
                JsonObject object = element.getAsJsonObject();
                if (object.has("id")) {
                    this.uuid = UUID.fromString(UUIDs.addDashes(object.get("id").getAsString()));
                    this.username = object.get("name").getAsString();
                    // If we get a UUID let's add them to our cache database
                    MUUIDCache.update(uuid, username);
                    return;
                }
            }
            throw new NullPointerException("Player not found");
        } else {
            this.username = name;
        }
    }

    public CacheProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public boolean exists() {
        return exists;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}


package gg.manny.brawl.player;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.statistic.PlayerStatistic;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.util.Cooldown;
import lombok.Data;
import lombok.NonNull;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerData {

    @NonNull
    private final UUID uniqueId;

    @NonNull
    private final String name;

    private PlayerState playerState;

    private boolean spawnProtection = true;

    private boolean warping = false;

    private Kit selectedKit;
    private Kit previousKit;

    private Map<String, Long> kitRentals = new HashMap<>();

    private PlayerStatistic statistic = new PlayerStatistic();

    private Map<String, Cooldown> cooldownMap = new HashMap<>();

    private boolean loaded;

    public Document toJSON() {
        Map<String, String> cooldownMap = new HashMap<>();
        this.cooldownMap.forEach((name, cooldown) -> cooldownMap.put(name, Pivot.GSON.toJson(cooldown.toJSON())));
        return new Document("uniqueId", this.uniqueId)
                .append("name", this.name)
                .append("previousKit", this.previousKit == null ? null : this.previousKit.getName())
                .append("cooldown", cooldownMap)
                .append("rentals", this.kitRentals)
                .append("statistic", this.statistic.toJSON());
    }

    public void fromJSON(Document document) {
        if(document == null) {
            this.loaded = true;
            this.save();
            return;
        }

        this.previousKit = Brawl.getInstance().getKitHandler().getKit(document.getString("previousKit"));

        if(document.containsKey("cooldown")) {
            Map<String, String> cooldownMap = (Map<String, String>) document.get("cooldown");
            cooldownMap.forEach((name, cooldownDocument) -> this.cooldownMap.put(name, new Cooldown(Pivot.GSON.fromJson(cooldownDocument, JsonObject.class))));
        }

        if(document.containsKey("rentals")) {
            this.kitRentals.putAll((Map<String, Long>) document.get("rentals"));
        }

        if(document.containsKey("statistic")) {
            statistic.fromJSON((Document) document.get("statistic"));
        }

        this.loaded = true;
    }

    public void save() {
        Brawl.getInstance().getPlayerDataHandler().setDocument(this.toJSON(), this.uniqueId);
    }

    public void cancelWarp() {

    }

    public PlayerState getPlayerState() {
        if(this.spawnProtection) {
            return PlayerState.SPAWN;
        }
        return PlayerState.FIGHTING;
    }

    public boolean hasKit(Kit kit) {
        if (this.kitRentals.containsKey(kit.getName()) && this.kitRentals.get(kit.getName()) < System.currentTimeMillis()) {
            this.kitRentals.remove(kit.getName());
        }

        return this.toPlayer().isOp() || kit.isFree() || this.toPlayer().hasPermission("kit." + kit.getName().toLowerCase()) ||  (kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis());
    }

    public Cooldown addCooldown(String cooldownName, long time) {
        Cooldown cooldown = this.getCooldown(cooldownName);
        if (cooldown != null) {
            cooldown.setExpire(cooldown.getExpire() + time);
        } else {
            cooldown = new Cooldown(time);
        }

        return this.cooldownMap.put(cooldownName, cooldown);
    }

    private Cooldown getCooldown(String cooldownName) {
        Cooldown cooldown = null;

        if (cooldownMap.containsKey(cooldownName.toUpperCase())) {
            cooldown = cooldownMap.get(cooldownName.toUpperCase());

            if (cooldown.hasExpired()) {
                cooldownMap.remove(cooldownName.toUpperCase());
                return null;
            }
        }

        return cooldown;
    }

    public boolean hasCooldown(String cooldownName) {
        return this.getCooldown(cooldownName) != null && !this.getCooldown(cooldownName).hasExpired();
    }

     Player toPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    @Override
    public String toString() {
        return "uniqueId=" + uniqueId.toString() + ";name=" + name;
    }
}
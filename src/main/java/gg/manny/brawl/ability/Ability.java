package gg.manny.brawl.ability;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.region.RegionType;
import gg.manny.pivot.util.Cooldown;
import gg.manny.pivot.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public abstract class Ability {

    private String name;

    private ItemStack icon;
    private int cooldown = 25;

    public Ability(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    public void onApply(Player player) {

    }

    public void onRemove(Player player) {

    }

    public void onActivate(Player player) {

    }

    public void onDeactivate(Player player) {

    }

    public Map<String, String> getProperties(Player player) {
        return new HashMap<>();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
//        JsonArray sidebarArray = new JsonArray();
//        for (String entry : this.getSidebar()) {
//            sidebarArray.add(new JsonPrimitive(entry));
//        }
        object.addProperty("name", this.name);
        object.addProperty("cooldown", this.cooldown);
        object.add("icon", ItemStackAdapter.serialize(this.icon));
        return object;
    }

    public void fromJson(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.cooldown = object.get("cooldown").getAsInt();
        this.icon = ItemStackAdapter.deserialize(object.get("icon"));
        //object.get("sidebar").getAsJsonArray().forEach(element -> this.getSidebar().add(element.getAsString()));
    }

    public boolean hasEquipped(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        return !RegionType.SAFEZONE.containsLocation(player.getLocation()) && playerData.getSelectedKit() != null && playerData.getSelectedKit().getAbilities().contains(this);
    }

    private long getCooldown() {
        return TimeUnit.SECONDS.toMillis(this.cooldown);
    }

    public boolean hasCooldown(Player player, boolean notify) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        String key = "ABILITY_" + this.name;
        Cooldown cooldown = playerData.getCooldown(key);
        boolean active = playerData.hasCooldown(key);

        if (active && notify) {
            player.sendMessage(Locale.PLAYER_ABILITY_COOLDOWN.format(cooldown.getTimeLeft()));
        }
        return active;
    }

    public void addCooldown(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.addCooldown("ABILITY_" + this.name, this.getCooldown());
        if (playerData.getEnderpearlTask() == null) {
            playerData.setEnderpearlTask(new BukkitRunnable() {

                final int playerLevel = player.getLevel();
                final Cooldown cooldown = toCooldown(playerData);

                @Override
                public void run() {

                    int timeLeft = (int) TimeUnit.MILLISECONDS.toSeconds(cooldown.getRemaining());
                    if (timeLeft <= 0 && playerData.getEnderpearlTask() != null) {
                        if (!cooldown.isNotified()) {
                            cooldown.setNotified(true);
                            player.sendMessage(Locale.PLAYER_ABILITY_EXPIRED.format(getName()));
                        }
                        this.cancel();
                        return;
                    }
                    player.setLevel(timeLeft);
                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();
                    player.setLevel(playerLevel);
                    playerData.setEnderpearlTask(null);
                }
            }.runTaskTimer(Brawl.getInstance(), 10L, 10L));
        }
    }

    public Cooldown toCooldown(PlayerData playerData) {
        return playerData.getCooldown("ABILITY_" + this.name);
    }

}

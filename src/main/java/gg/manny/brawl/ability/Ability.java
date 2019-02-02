package gg.manny.brawl.ability;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.util.Cooldown;
import gg.manny.pivot.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

@Data
public abstract class Ability {

    private String name;

    private ItemStack icon;

    public Ability(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    public void onActivate(Player player) {

    }

    public void onDeactivate(Player player) {

    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", this.name);
        object.add("icon", ItemStackAdapter.serialize(this.icon));
        return object;
    }

    public void fromJson(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.icon = ItemStackAdapter.deserialize(object.get("icon"));
    }

    private long getCooldown() {
        return TimeUnit.SECONDS.toMillis(25L);
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
    }

}

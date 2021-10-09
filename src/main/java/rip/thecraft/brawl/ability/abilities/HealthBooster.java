package rip.thecraft.brawl.ability.abilities;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.util.SchedulerUtil;

public class HealthBooster extends Ability implements Listener {

    private double boost = 40;

    @Override
    public void onApply(Player player) {
        SchedulerUtil.runTask(() -> {
            player.setMaxHealth(24);
            player.setHealth(player.getMaxHealth());
        }, false);
    }

    @Override
    public void onRemove(Player player) {
        SchedulerUtil.runTask(() -> {
            if (player != null) {
                player.setMaxHealth(20);
                if (!player.isDead()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
        }, false);
    }

    @Override
    public void onKill(Player player) {
        double newHealth = player.getMaxHealth() + 4;

        if (newHealth <= boost) {
            player.setMaxHealth(newHealth);
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("boost", this.boost);
        return object;
    }

    @Override
    public void fromJson(JsonObject object) {
        super.fromJson(object);
        this.boost = object.get("boost").getAsDouble();

    }
}

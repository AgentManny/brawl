package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.ability.Ability;
import org.bukkit.entity.Player;

public class HealthBooster extends Ability {

    private double boost = 40;

    @Override
    public void onApply(Player player) {
        player.setMaxHealth(24);
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public void onRemove(Player player) {
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
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

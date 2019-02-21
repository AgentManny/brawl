package gg.manny.brawl.ability.type;

import com.google.gson.JsonObject;
import gg.manny.brawl.ability.Ability;
import org.bukkit.entity.Player;

public class HealthBooster extends Ability {

    private double boost = 40;

    public HealthBooster() {
        super("HealthBooster", null);
    }

    @Override
    public void onApply(Player player) {
        player.setMaxHealth(boost);
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public void onRemove(Player player) {
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
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

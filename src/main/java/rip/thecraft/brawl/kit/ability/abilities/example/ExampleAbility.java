package rip.thecraft.brawl.kit.ability.abilities.example;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.handlers.AbilityEvent;
import rip.thecraft.brawl.kit.ability.handlers.AbilityPlayer;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;

@AbilityData(
        name = "Example",
        description = "An example of how ability should function",
        icon = Material.DIRT, color = ChatColor.RED
)
public class ExampleAbility extends Ability {

    @AbilityProperty(id = "power", description = "Power of ability")
    public double power = 3;

    @AbilityProperty(id = "cool-variable", description = "Cool of ability")
    public String coolVariable = "This is a pretty cool variable";

    @AbilityProperty(id = "sound", description = "Sound")
    public Sound clickSound = Sound.CLICK;

    public void onApply(Player player) {

    }

    @AbilityEvent
    public void onGround(@AbilityPlayer(spawnProtection = true) Player player, boolean onGround) {
        player.sendMessage("Player is on ground: " + onGround);
    }
}

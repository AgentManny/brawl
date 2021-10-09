package rip.thecraft.brawl.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.meta.AbilityInfo;
import rip.thecraft.brawl.ability.meta.AbilityProperty;

@AbilityInfo(name = "Example", description = "An example of how ability should function", icon = Material.DIRT, color = ChatColor.RED)
public class ExampleAbility extends Ability {

    @AbilityProperty(id = "power", description = "Power of ability")
    private int power = 3;

    public void onApply(Player player) {

    }

}

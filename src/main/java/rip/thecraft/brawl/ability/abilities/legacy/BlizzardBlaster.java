package rip.thecraft.brawl.ability.abilities.legacy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;

@AbilityData(
        name = "Blizzard Blaster",
        icon = Material.PACKED_ICE,
        color = ChatColor.AQUA
)
@Deprecated
public class BlizzardBlaster extends Ability {

    @AbilityProperty
    public int radius = 5;

    @AbilityProperty
    public int time = 15;

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;

        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to activate this ability.");
            return;
        }

        this.addCooldown(player);
    }
}

package rip.thecraft.brawl.kit.ability.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.Ability;
import rip.thecraft.brawl.kit.ability.handlers.KillHandler;
import rip.thecraft.brawl.kit.ability.property.AbilityData;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;

@AbilityData(
        name = "Dash",
        description = "Gain a short burst of speed after killing a player",
        color = ChatColor.DARK_AQUA,
        icon = Material.SUGAR,
        displayIcon = false
)
public class Dash extends Ability implements Listener, KillHandler {

    @AbilityProperty(id = "duration", description = "Duration of Speed in ticks")
    public int duration = 50;

    @AbilityProperty(id = "amplifier", description = "Speed amplifier")
    public int amplifier = 4;

    @Override
    public void onKill(Player killer, Player victim) {
        Brawl.getInstance().getEffectRestorer().setRestoreEffect(killer, new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
    }
}

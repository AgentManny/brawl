package rip.thecraft.brawl.spawn.event.events;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.spawn.event.type.TimeEvent;

public class KitFrenzy extends TimeEvent {

    @AbilityProperty(id = "abilities-only") public boolean abilitiesOnly = true; // Only allow kits that have abilities

    public KitFrenzy(String name) {
        super(name, EventType.KIT_FRENZY);
    }

    @Override
    public void start() {

    }

    @Override
    public void onSpawnLeave(Player player, PlayerData playerData) {
        if (!playerData.isWarp() && playerData.getPlayerState() == PlayerState.FIGHTING) {
            Kit kit = Brawl.getInstance().getKitHandler().getRandomAbilityKit();
            kit.apply(player, true, true,
                    ChatColor.GREEN.toString() + ChatColor.BOLD + "FRENZY EVENT " + ChatColor.WHITE + "Your kit is now " + ChatColor.YELLOW + kit.getName() + ChatColor.WHITE + "!");
            player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1f, 1f);

        }
    }

    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public void finish() {

    }
}

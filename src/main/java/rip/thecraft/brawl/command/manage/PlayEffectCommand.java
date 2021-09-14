package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class PlayEffectCommand {

    @Command(names = "playmobeffect")
    public static void playMobEffect(Player sender, EntityEffect effect, @Param(defaultValue = "self") Player target) {
        sender.sendMessage(ChatColor.GREEN + "Playing effect: " + effect.name());
        target.playEffect(effect);
    }

    @Command(names = "playeffect")
    public static void playEffect(Player sender, Effect effect, @Param(defaultValue = "self") Player target, @Param(defaultValue = "1") int i) {
        sender.sendMessage(ChatColor.GREEN + "Playing effect: " + effect.name());
        target.playEffect(target.getLocation(), effect, i);
    }

}

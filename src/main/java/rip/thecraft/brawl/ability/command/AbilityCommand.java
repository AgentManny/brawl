package rip.thecraft.brawl.ability.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.type.Illusioner;
import rip.thecraft.brawl.util.Human;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.spartan.command.Command;

import java.util.HashSet;
import java.util.List;

public class AbilityCommand {

    @Command(names = { "ability", "a" }, permission = "brawl.command.ability")
    public void execute(Player sender, Ability ability) {
        sender.sendMessage(ChatColor.GOLD + "Manually activated " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + "...");
        ability.onActivate(sender);
    }

    @Command(names = { "ability cooldown", "a cooldown" }, permission = "brawl.command.ability")
    public void execute(Player sender, Ability ability, int cooldown) {
        sender.sendMessage(ChatColor.GOLD + "Changed " + ChatColor.WHITE + ability.getName() + ChatColor.GOLD + " cooldown to " + cooldown + " seconds.");
        ability.setCooldown(cooldown);
        Brawl.getInstance().getAbilityHandler().save();
    }

    @Command(names = { "debug effect", "debug effect" }, permission = "brawl.command.ability")
    public void execute(Player sender, String source) {
        ParticleEffect effect = ParticleEffect.fromName(source);
        if (effect == null) {
            sender.sendMessage("");
            return;
        }
        List<Block> blocks = sender.getLineOfSight(new HashSet<Material>(), 10);
        for(Block block : blocks) {
            effect.send(block.getLocation(), 0.1f, 1);
        }
    }

    @Command(names = { "debug walk", "debug walk" }, permission = "brawl.command.ability")
    public void test(Player sender, double x, double y, double z) {
        for (Human registeredHuman : Illusioner.registeredHumans) {
            registeredHuman.walk(x, y, z, sender.getLocation().getYaw(), sender.getLocation().getPitch());
        }
    }
}

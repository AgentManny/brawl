package rip.thecraft.brawl.ability.abilities;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.spartan.nametag.NametagHandler;

import java.util.Arrays;
import java.util.Collections;

@AbilityData(
        name = "Stealth Mode",
        description = "Enter your stealth state and deal increased damage, at the cost of your armor.",
        icon = Material.SULPHUR,
        color = ChatColor.BLUE
)
public class StealthMode extends Ability implements Listener {

    private static String STEALTH_METADATA = "Stealth";

    @AbilityProperty(id = "duration", description = "Duration in ticks for stealth")
    public int duration = 110;

    @AbilityProperty(id = "strength-duration", description = "Strength Duration in ticks")
    public int strengthDuration = 80;

    @AbilityProperty(id = "strength-amplifier", description = "Strength potion amplifier")
    public int strengthAmplifier = 0;

    @AbilityProperty(id = "show-effects", description = "Show potion effects")
    public boolean showEffects = false;

    @Override
    public void onActivate(Player player) {
        if (hasCooldown(player, true)) return;
        addCooldown(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, showEffects, showEffects));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, strengthDuration, strengthAmplifier, showEffects, showEffects));

        int heldItemSlot = player.getInventory().getHeldItemSlot();
        player.getInventory().setItem(heldItemSlot, null);
        player.updateInventory();

        player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now in your stealth state!");
        player.sendMessage(ChatColor.GRAY + "You are hidden from normal players and your damage has increased.");

        // Removes nametag for newer versions while invisible
        Scoreboard scoreboard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();
        ScoreboardTeam team = scoreboard.getTeam(STEALTH_METADATA);
        if (team == null) {
            team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), STEALTH_METADATA);
        }
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;

            Arrays.asList(
                    new PacketPlayOutScoreboardTeam(team, 1),
                    new PacketPlayOutScoreboardTeam(team, 0),
                    new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3)
            ).forEach(packet -> ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(packet));
        }

        int taskId = new BukkitRunnable() {

            private ItemStack[] armorCopy = player.getInventory().getArmorContents().clone();

            @Override
            public void run() {
                if (player != null && hasEquipped(player)) {
                    player.getInventory().setArmorContents(armorCopy);
                    player.getInventory().setItem(heldItemSlot, getIcon());
                    player.removeMetadata(STEALTH_METADATA, Brawl.getInstance());
                    player.sendMessage(ChatColor.GREEN + "You've returned to your normal state.");
                    player.updateInventory();
                    NametagHandler.reloadPlayer(player);
                }
            }

        }.runTaskLater(Brawl.getInstance(), duration).getTaskId();

        player.getInventory().setArmorContents(null);
        player.setMetadata(STEALTH_METADATA, new FixedMetadataValue(Brawl.getInstance(), taskId));
    }

    @Override
    public void onDeactivate(Player player) {
        if (player.hasMetadata(STEALTH_METADATA)) {
            int taskId = player.getMetadata(STEALTH_METADATA, Brawl.getInstance()).asInt();
            if (Brawl.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId)) {
                Brawl.getInstance().getServer().getScheduler().cancelTask(taskId);
            }
        }
    }
}
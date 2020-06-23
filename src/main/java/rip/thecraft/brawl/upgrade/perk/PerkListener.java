package rip.thecraft.brawl.upgrade.perk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.player.event.PlayerKillEvent;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.brawl.util.MathUtil;
import rip.thecraft.spartan.deathmessage.event.CustomPlayerDamageEvent;
import rip.thecraft.spartan.deathmessage.tracker.FallTracker;

import static rip.thecraft.brawl.upgrade.perk.Perk.*;

public class PerkListener implements Listener {

    @EventHandler
    public void onProjectileLaunch(EntityShootBowEvent event) {
        if (event.getActor() instanceof Player && event.getEntity() instanceof Arrow) {
            Player player = (Player) event.getActor();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.getPlayerState() == PlayerState.FIGHTING && playerData.usingPerk(BLAZING_ARROWS)) {
                event.getEntity().setFireTicks(Integer.MAX_VALUE);
            }
        }
    }

    @EventHandler
    public void onFallDamage(CustomPlayerDamageEvent customEvent) {
        EntityDamageEvent event = customEvent.getCause();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = customEvent.getPlayer();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.getUnlockedPerks().isEmpty() && !(customEvent.getTrackerDamage() instanceof FallTracker.FallDamageByPlayer))
                return; // Don't proceed to check every perk if they don't have any.

            if (playerData.usingPerk(LIGHTWEIGHT)) {
                player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.YELLOW + "Lightweight" + ChatColor.GREEN + " perk reduced your fall damage by 50%.");
                event.setDamage(event.getDamage() / 2.0); // 50%
            }
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        if (event.getState() != PlayerState.FIGHTING) return; // Ensures they are in Spawn
        Player player = event.getPlayer();
        Player victim = event.getVictim();

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.getUnlockedPerks().isEmpty()) return; // Don't proceed to check every perk if they don't have any.

        int probability = MathUtil.getRandomProbability();
        if (playerData.usingPerk(NOURISHMENT)) {
            if (probability <= 25) {
                ItemStack refillItem = playerData.getRefillType().getItem();
                ItemStack[] items = player.getInventory().getContents();
                for (int i = 0; i < 9; i++) {
                    if (items[i] == null || items[i].getType() == Material.AIR || items[i].getType() == Material.BOW) {
                        items[i] = refillItem;
                    }
                }
                player.getInventory().setContents(items);
                player.updateInventory();
                player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.YELLOW + "Nourishment" + ChatColor.GREEN + " perk instantly replenished your hotbar.");
            }
        }

//        if (playerData.usingPerk(Perk.))

        if (playerData.usingPerk(MEDIC)) {
            Team team = playerData.getTeam();
            if (team != null) {
                if (!team.isMember(victim)) { // Make sure they didn't kill their own teammates
                    int healed = 0;
                    for (Player nearbyPlayer : BrawlUtil.getNearbyPlayers(player, 15)) {
                        if (team.isMember(nearbyPlayer)) {
                            nearbyPlayer.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.GREEN + " healed you using his " + ChatColor.YELLOW + "Medic" + ChatColor.GREEN + " perk.");
                            nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                            healed++;
                        }
                    }
                    if (healed > 0) {
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.GREEN + "Medic" + ChatColor.GREEN + " perk healed " + healed + " players.");
                    }
                }
            // } else {
                // player.sendMessage(ChatColor.RED + "Uh oh! It appears you are not in a team, Medic perk did not apply.");
            }
        }


    }



}

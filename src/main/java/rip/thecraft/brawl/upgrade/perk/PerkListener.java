package rip.thecraft.brawl.upgrade.perk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.event.AbilityCooldownEvent;
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
            if (playerData.getPlayerState() == PlayerState.FIGHTING && playerData.usingPerk(BLAZING_ARROWS) && MathUtil.getRandomProbability() <= 30) {
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

            if (playerData.getPlayerState() != PlayerState.FIGHTING && playerData.getUnlockedPerks().isEmpty() && !(customEvent.getTrackerDamage() instanceof FallTracker.FallDamageByPlayer))
                return; // Don't proceed to check every perk if they don't have any.

            if (playerData.usingPerk(LIGHTWEIGHT)) {
                player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.AQUA + "Lightweight" + ChatColor.GREEN + " perk reduced your fall damage by 50%.");
                event.setDamage(event.getDamage() / 2.0); // 50%
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location playerLoc = player.getLocation();
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData.getPlayerState() != PlayerState.FIGHTING) return; // Ensures they are in Spawn

        if (playerData.usingPerk(KAMIKAZE)) {
            player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.DARK_RED + "Kamikaze" + ChatColor.GREEN + " perk spawned a Charged Creeper on your death location.");
            Creeper creeper = player.getWorld().spawn(playerLoc, Creeper.class);
            creeper.setPowered(true);
            creeper.setMetadata("Owner", new FixedMetadataValue(Brawl.getInstance(), player.getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDamager() instanceof Player) {
                Player victim = (Player) event.getDamager();
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                if (playerData.getPlayerState() != PlayerState.FIGHTING) return; // Ensures they are in Spawn

                double probability = MathUtil.getRandomProbability();

                if (probability <= 5) {
                    if (playerData.usingPerk(REAPER)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0)); // Regen I for 3 seconds
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1)); // Wither II for 2 seconds
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.DARK_GRAY + "Reaper" + ChatColor.GREEN + " perk  " + ChatColor.WHITE + victim.getDisplayName() + ChatColor.GREEN + ".");

                    }
                    if (playerData.usingPerk(VENOM)) {
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1)); // Poison II for 3 seconds
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.DARK_GREEN  + "Venom" + ChatColor.GREEN + " perk poisoned " + ChatColor.WHITE + victim.getDisplayName() + ChatColor.GREEN + ".");
                    }
                } else if (probability <= 30 && playerData.usingPerk(ADRENALINE)) {
                    if (player.getHealth() <= 7) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1)); // Damage Resistance II for 3 seconds
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1)); // Speed II for 3 seconds
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.RED + "Adrenaline" + ChatColor.GREEN + " perk has energised you with Regen and Speed.");
                    }
                }
            } else if (event.getEntity().hasMetadata("Owner")) {
                Player damager = Bukkit.getPlayer(event.getEntity().getMetadata("Owner", Brawl.getInstance()).asString());
                if (damager != null) {
                    player.setLastDamageCause(new EntityDamageByEntityEvent(player, damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK, event.getDamage()));
                }
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

        double probability = MathUtil.getRandomProbability();
        if (playerData.usingPerk(NOURISHMENT) && probability <= 25) {
            ItemStack refillItem = playerData.getRefillType().getItem();
            ItemStack[] items = player.getInventory().getContents();
            for (int i = 0; i < 9; i++) {
                if (items[i] == null || items[i].getType() == Material.AIR || items[i].getType() == Material.BOWL) {
                    player.getInventory().setItem(i, refillItem);
                }
            }
            player.getInventory().setContents(items);
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.YELLOW + "Nourishment" + ChatColor.GREEN + " perk instantly replenished your hotbar.");
        }

        if (playerData.usingPerk(BULLDOZER)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)); // Damage Strength I for 5 seconds
            player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.RED + "Bulldozer" + ChatColor.GREEN + " perk gave you Strength I for 5 seconds.");
        }

        if (playerData.usingPerk(JUGGERNAUT)) {
            int regenTime = MathUtil.getRandomInt(3, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, regenTime * 20, 0)); // Regen I for #regenTime seconds
            player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.LIGHT_PURPLE + "Juggernaut" + ChatColor.GREEN + " perk gave you regeneration.");
        }

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
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.YELLOW + "Medic" + ChatColor.GREEN + " perk healed " + healed + " allies.");
                    }
                }
            // } else {
                // player.sendMessage(ChatColor.RED + "Uh oh! It appears you are not in a team, Medic perk did not apply.");
            }
        }
    }

    @EventHandler
    public void onAbilityActivate(AbilityCooldownEvent event) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(event.getPlayer().getUniqueId());
        if (playerData.usingPerk(OVERCLOCK)) {
            playerData.setCooldown("ABILITY_" + event.getAbility().getName(), (long) (event.getCooldown() * 0.5));
            event.getPlayer().sendMessage(ChatColor.GREEN + "Your " + ChatColor.YELLOW + "Overclock" + ChatColor.GREEN + " perked reduced your " + event.getAbility().getName() + " cooldown.");
        }
    }



}

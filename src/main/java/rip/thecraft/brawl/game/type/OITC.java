package rip.thecraft.brawl.game.type;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.DurationFormatter;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.brawl.util.SchedulerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.Cooldown;
import rip.thecraft.spartan.util.PlayerUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OITC extends Game implements Listener {

    public OITC() {
        super(GameType.OITC, GameFlag.NO_FALL, GameFlag.WATER_ELIMINATE);
    }

    private Map<GamePlayer, Integer> killsMap;

    private List<UUID> boostCooldown = new ArrayList<>();
    private List<UUID> cooldown = new ArrayList<>();

    @Override
    public void setup() {
        this.killsMap = new HashMap<>();
        Collections.shuffle(players);

        this.boostCooldown.clear();
        this.cooldown.clear();

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            respawn(player, 0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 2));
            killsMap.put(gamePlayer, 0);
        });
        this.broadcast(Game.PREFIX + ChatColor.YELLOW + "This event supports " + ChatColor.LIGHT_PURPLE + "Avatar Jump" + ChatColor.YELLOW + ", you can use this ability by Jumping and Shifting.");

        this.startTimer(5, true);
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.DARK_PURPLE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);
        if (this.state == GameState.STARTED) {
            LinkedHashMap<GamePlayer, Integer> sortedKills = sortByValues(killsMap);
            int index = 0;

            for (Map.Entry<GamePlayer, Integer> entry : sortedKills.entrySet()) {
                index++;
                if (index > 5) break;

                toReturn.add(CC.DARK_PURPLE + index + ". " + CC.WHITE + entry.getKey().getName() + " " + CC.LIGHT_PURPLE + entry.getValue());
            }

            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.hasCooldown("Grace Period") || playerData.hasCooldown("Double Jump")) {
                toReturn.add(CC.GREEN + CC.SCOREBAORD_SEPARATOR);
            }
            if (playerData.hasCooldown("Grace Period")) {
                toReturn.add(CC.DARK_PURPLE + "Grace Period: " + CC.LIGHT_PURPLE +  DurationFormatter.getRemaining(playerData.getCooldown("Grace Period").getRemaining()));
            }
            if (playerData.hasCooldown("Double Jump")) {
                toReturn.add(CC.DARK_PURPLE + "Double Jump: " + CC.LIGHT_PURPLE +  DurationFormatter.getRemaining(playerData.getCooldown("Double Jump").getRemaining()));
            }
        } else if (this.state == GameState.FINISHED) {
            boolean winners = this.winners.size() > 1;
            if (winners) {
                toReturn.add(CC.DARK_PURPLE + "Winners: ");
                for (GamePlayer winner : this.winners) {
                    toReturn.add(CC.LIGHT_PURPLE + "  " + winner.getName());
                }
            } else if (!this.winners.isEmpty()) {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.LIGHT_PURPLE + this.winners.get(0).getName());
            } else {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.RED + "None");
            }
        } else {
            toReturn.add(CC.LIGHT_PURPLE + "Waiting...");
        }
        return toReturn;
    }

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (elimination == GameElimination.WATER) {
            if (!getGamePlayer(player).toPlayerData().hasCooldown("Grace Period") && state == GameState.STARTED) {
                player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "You've lost a kill because you fell in water.");
                removeKill(getGamePlayer(player));
                respawn(player, 0);
            }
        } else if (elimination != GameElimination.PLAYER) {
            super.handleElimination(player, location, elimination);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (game instanceof OITC && containsPlayer(victim)) {
            event.setDroppedExp(0);
            event.getDrops().clear();

            if (killer != null && containsPlayer(killer)) {
                onKill(killer, victim, 10);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (!(game instanceof OITC)) return;

        Player victim = (Player) event.getEntity();
        Player shooter = null;
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                shooter = (Player) projectile.getShooter();
            }
        }


        GamePlayer gameVictim = getGamePlayer(victim);

        if (gameVictim != null) {
            GamePlayer gameShooter = getGamePlayer(shooter);
            if (shooter != null && gameShooter != null) {
                if (shooter == victim) {
                    event.setCancelled(true);
                    return;
                }

                if (handlePrevention(shooter, victim, gameShooter, true)) {
                    event.setCancelled(true);
                    shooter.getInventory().addItem(new ItemStack(Material.ARROW));
                    return;
                }

                event.setDamage(0);

                onKill(shooter, victim, 0);

            } else if (event.getDamager() instanceof Player){
                Player damager = (Player) event.getDamager();
                GamePlayer gamePlayer = getGamePlayer(damager);
                if (gamePlayer != null && handlePrevention(damager, victim, gamePlayer, true)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean handlePrevention(Player player, Player victim, GamePlayer gamePlayer, boolean notify) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        PlayerData victimData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(victim);
        if (this.state != GameState.STARTED) {
            return true;
        }

        if (playerData.hasCooldown("Grace Period")) {
            if (notify) {
                player.sendMessage(ChatColor.RED + "You can't damage " + victim.getName() + " as you're on grace period.");
            }
            return true;
        }

        if (victimData.hasCooldown("Grace Period")) {
            if (notify) {
                player.sendMessage(ChatColor.RED + victim.getName() + " cannot be damaged due to their grace period.");
            }
            return true;
        }

        return false;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
        if (game instanceof OITC) {
            if (event.isSneaking() && containsPlayer(player) && getGamePlayer(player).isAlive()) {
                if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                    if (playerData.hasCooldown("Double Jump")) {
                        player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + playerData.getCooldown("Double Jump").getTimeLeft() + ChatColor.RED + " before using this again.");
                        return;
                    }

                    playerData.addCooldown("Double Jump", TimeUnit.SECONDS.toMillis(15));
                    player.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(2).setY(1));
                    player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
                }
            }
        }
    }

    public void onKill(Player killer, Player victim, int delay) {
        GamePlayer gameKiller = getGamePlayer(killer);
        GamePlayer gameVictim = getGamePlayer(victim);

        addKill(gameKiller);
        respawn(victim, delay);

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(victim);
        playerData.addCooldown("Grace Period", TimeUnit.SECONDS.toMillis(3L));
        Cooldown cd = playerData.getCooldown("Double Jump");
        if (cd != null) {
            cd.setExpire(0);
            cd.setNotified(true);
        }
        ((CraftPlayer) victim).getHandle().getDataWatcher().watch(9, (byte) 0);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 2));

        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 2));
        killer.getInventory().addItem(new ItemStack(Material.ARROW));
        killer.updateInventory();

        this.broadcast(Game.PREFIX + ChatColor.WHITE + victim.getName() + ChatColor.GRAY + "[" + getKills(gameVictim) + ChatColor.GRAY + "]" + ChatColor.YELLOW + " was killed by " + ChatColor.WHITE + killer.getName() + ChatColor.GRAY + "[" + getKills(gameKiller) + ChatColor.GRAY + "]" + ChatColor.YELLOW + ".");
    }

    public void respawn(Player player, int delay) {
        if (player == null) return;

        Runnable runnable = () -> {
            PlayerUtil.resetInventory(player, GameMode.SURVIVAL);
            player.teleport(this.getRandomLocation());
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta meta = item.getItemMeta();

            meta.spigot().setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);

            item = new ItemStack(Material.WOOD_PICKAXE);
            meta = item.getItemMeta();
            meta.spigot().setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            player.updateInventory();


            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 2));
        };

        if (delay == 0) {
            runnable.run();
        } else {
            SchedulerUtil.runTaskLater(runnable, delay, false);
        }
    }

    public void addKill(GamePlayer player) {
        int kills = getKills(player) + 1;
        this.killsMap.put(player, kills);

        if (kills == 20) {
            // Find a winner
            this.winners.add(player);
            this.end();
        } else if (kills == 19) {
            broadcast(Game.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " needs " + ChatColor.LIGHT_PURPLE + "1" + ChatColor.YELLOW + " more kills to win!");
        }
    }

    public void removeKill(GamePlayer player) {
        killsMap.put(player, Math.max(getKills(player) - 1, 0));
    }

    public int getKills(GamePlayer player) {
        return this.killsMap.getOrDefault(player, 0);
    }

    private LinkedHashMap<GamePlayer, Integer> sortByValues(Map<GamePlayer, Integer> map) {
        LinkedList<Map.Entry<GamePlayer, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<GamePlayer, Integer> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<GamePlayer, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


}

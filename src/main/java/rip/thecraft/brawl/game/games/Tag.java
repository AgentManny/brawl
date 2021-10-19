package rip.thecraft.brawl.game.games;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.util.ParticleEffect;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Tag extends Game implements Listener {

    public Tag() {
        super(GameType.TNT_TAG, GameFlag.NO_FALL, GameFlag.NO_DAMAGE);
    }

    private List<GamePlayer> taggers;

    private int explodeTimer;
    private int defaultTime;

    private int round;

    private BukkitTask task;
    private BukkitTask explosionTask;

    @Override
    public void setup() {
        this.setDefaultLocation(this.getLocationByName("Lobby"));

        this.round = 0;
        this.defaultTime = 60;
        this.taggers = new ArrayList<>();

        Collections.shuffle(players);

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(this.getLocationByName("Lobby"));
        });

        state = GameState.STARTED;
        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::startRound, 60L);
    }

    public void startRound() {
        if (this.state == GameState.ENDED || this.state == GameState.FINISHED) return;

        round++;
        this.explodeTimer = Math.min(60, this.defaultTime / round);

        state = GameState.GRACE_PERIOD;


        if (task != null) {
            task.cancel();
            task = null;
        }

        setTime(3);
        task = new BukkitRunnable() {
            public void run() {
                if (getTime() == 0) {
                    selectTaggers();
                    setTime(-1);
                    this.cancel();
                    return;
                }

                switch (getTime()) {
                    case 3:
                    case 2:
                    case 1:

                        playSound(Sound.NOTE_PIANO, 1L, 1L);
                        broadcast(Game.PREFIX + ChatColor.WHITE + (round == 1 ? "First Round" : (getAlivePlayers().size() < 2 ? "Final Round" : "Round #" + round)) + ChatColor.YELLOW + " will start in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(getTime()) + ChatColor.YELLOW + ".");
                        break;
                    default:
                        break;
                }

                setTime(getTime() - 1);
            }
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);


    }

    public void selectTaggers() {
        taggers.clear();

        List<String> playersFormat = new ArrayList<>();
        int taggers = Math.round(this.getAlivePlayers().size() / 2);


        for (GamePlayer gamePlayer : this.getAlivePlayers()) {
            Player player = gamePlayer.toPlayer();

            updatePlayer(player, false);
        }

        for (int i = 0; i < taggers; i++) {

            GamePlayer gamePlayer = this.getAlivePlayers().stream().filter(u -> u.toPlayer() != null && !this.taggers.contains(u)).collect(Collectors.toList()).get(Brawl.RANDOM.nextInt(this.getAlivePlayers().size()));
            this.taggers.add(gamePlayer);
            playersFormat.add(gamePlayer.getName());

            if (gamePlayer.toPlayer() != null) {
                Player player = gamePlayer.toPlayer();

                player.getWorld().createExplosion(player.getLocation(), 0F, false);
                player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "You have started as IT.");
                updatePlayer(player, true);
            }
        }

        if (this.taggers.isEmpty()) {
            end();
            return;
        }

        state = GameState.STARTED;
        this.broadcast(Game.PREFIX + ChatColor.YELLOW + "Taggers: " + ChatColor.LIGHT_PURPLE + StringUtils.join(playersFormat, ", "));
        this.startExplosionTimer();
    }

    public void startExplosionTimer() {
        if (explosionTask != null) {
            explosionTask.cancel();
            explosionTask = null;
        }

        explosionTask = new BukkitRunnable() {
            public void run() {
                if (state == GameState.FINISHED || state == GameState.ENDED) {
                    this.cancel();
                    return;
                }
                if (explodeTimer <= 0) {
                    explode();

                    this.cancel();
                    return;
                }

                switch (explodeTimer) {
                    case 60:
                    case 45:
                    case 30:
                    case 20:
                    case 15:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        playSound(Sound.NOTE_PIANO, 1L, 1L);
                        broadcast(Game.PREFIX + ChatColor.WHITE + "Taggers" + ChatColor.YELLOW + " will explode in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(explodeTimer) + ChatColor.YELLOW + ".");

                    default:
                        break;
                }
                explodeTimer--;

            }
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
    }

    @Override
    public String handleNametag(Player toRefresh, Player refreshFor) {
        if (taggers != null && containsPlayer(toRefresh) && taggers.contains(getGamePlayer(toRefresh))) {
            return CC.RED;
        }

        return super.handleNametag(toRefresh, refreshFor);
    }

    public void explode() {
        Iterator<GamePlayer> iterator = taggers.iterator();
        while (iterator.hasNext()) {
            GamePlayer tagged = iterator.next();
            Player player = tagged.toPlayer();

            Arrays.asList(ParticleEffect.SMOKE_LARGE, ParticleEffect.EXPLOSION_NORMAL, ParticleEffect.LAVA).forEach(effect -> effect.display(0, 0, 0, 0, 1, player.getLocation(), 25));

            player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "You blew up.");
            player.setHealth(0);
            iterator.remove();
        }

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::startRound, 60L);
        this.playSound(Sound.EXPLODE, 1.0F, 1.0F);
    }

    @Override
    public String getEliminateMessage(Player player, GameElimination elimination) {
        return ChatColor.DARK_RED + player.getName() + ChatColor.RED + (elimination == GameElimination.QUIT ? " disconnected" : " blew up.") + ".";
    }

    @Override
    public void handleElimination(Player player, Location location, GameElimination elimination) {
        if (eliminate(player, location, elimination)) {
            // Find a winner
            if (this.getAlivePlayers().size() == 1) {
                GamePlayer winner = this.getAlivePlayers().get(0);
                this.winners.add(winner);

                this.end();
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Tag) {
                GamePlayer gamePlayer = this.getGamePlayer(player);
                GamePlayer gameDamager = this.getGamePlayer(damager);
                if (gamePlayer != null && gameDamager != null) {
                    if (gamePlayer.isAlive() && gameDamager.isAlive()) {
                        event.setDamage(0);

                        if (taggers.contains(gameDamager)) {

                            taggers.remove(gameDamager);
                            taggers.add(gamePlayer);

                            updatePlayer(damager, false);
                            updatePlayer(player, true);

                            this.broadcast(Game.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " is now IT!");
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game instanceof Tag) {
                GamePlayer gamePlayer = this.getGamePlayer(player);
                if (gamePlayer != null && gamePlayer.isAlive() && taggers.contains(gamePlayer)) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        }
    }

    public static final ItemStack ICON = new ItemBuilder(Material.TNT)
            .name(ChatColor.RED + "You are tagged!")
            .build();

    public void updatePlayer(Player player, boolean tagged) {
        player.removePotionEffect(PotionEffectType.SPEED);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, tagged ? 2 : 1));
        player.setSprinting(true);

        ItemStack item = tagged ? ICON : null;
        player.getInventory().setArmorContents(new ItemStack[] { item, item, item, item });
        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, item);
        }
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.DARK_PURPLE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.DARK_PURPLE + "Round: " + CC.LIGHT_PURPLE + this.round + (this.state == GameState.FINISHED ? "" : (getTime() >= 0 ? CC.GRAY + " (" + getTime() + "s)" : "")));
        toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);
        if (this.state == GameState.STARTED) {
            toReturn.add(CC.DARK_PURPLE + "Explosion in " + ChatColor.LIGHT_PURPLE + explodeTimer + "s" + CC.DARK_PURPLE + "");
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
}

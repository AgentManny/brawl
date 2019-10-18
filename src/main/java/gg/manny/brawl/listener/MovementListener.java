package gg.manny.brawl.listener;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.match.Match;
import gg.manny.brawl.duelarena.match.MatchState;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.PlayerState;
import gg.manny.brawl.region.RegionType;
import gg.manny.pivot.staff.StaffMode;
import gg.manny.server.handler.SimpleMovementHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class MovementListener implements SimpleMovementHandler, Listener {

    private final Brawl plugin;

    @Override
    public void onPlayerMove(Player player, Location to, Location from) {

        if (to.getX() != from.getX() || to.getZ() != from.getZ() || to.getY() != from.getY()) {
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
            if (playerData == null) return;

            PlayerState state = playerData.getPlayerState();

            if (playerData.isWarping()) {
                if (from.distance(to) > 0.1) {
                    playerData.cancelWarp();
                }
            }

            if (to.getY() < 0) {
                switch (state) {
                    case GAME_LOBBY: {
                        player.teleport(plugin.getLocationByName("GAME_LOBBY"));
                        break;
                    }
                    default: {
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            player.setHealth(0.0);
                        }
                        break;
                    }
                }
            }

            Match match = Brawl.getInstance().getMatchHandler().getMatch(player);
            if (match != null) {
                if (match.getState() == MatchState.FIGHTING) {
                    if (to.getBlock().isLiquid()) {
                        match.eliminated(player);
                        player.setFireTicks(0);
                        return;
                    }
                } else if (match.getState() == MatchState.GRACE_PERIOD) {
                    if (from.distance(to) > 0.1) {
                        player.teleport(from);
                        player.setFallDistance(0);
                    }
                }
            }

            Game game = Brawl.getInstance().getGameHandler().getActiveGame();
            if (game != null && game.containsPlayer(player)) {
                GamePlayer gamePlayer = game.getGamePlayer(player);
                if (gamePlayer.isAlive() && game.getFlags().contains(GameFlag.WATER_ELIMINATE)) {
                    if (to.getBlock().isLiquid()) {
                        game.handleElimination(player, to, false);
                    }
                }
            }


            if (playerData.isDuelArena() && playerData.isSpawnProtection()) {
                playerData.setSpawnProtection(false);
            }

            if  (StaffMode.hasStaffMode(player)) return;

            if (playerData.isSpawnProtection()) {

                if (RegionType.SAFEZONE.appliesTo(from) && !RegionType.SAFEZONE.appliesTo(to)) {
                    playerData.setSpawnProtection(false);
                    if (playerData.getSelectedKit() == null) {
                        Kit selectedKit = Brawl.getInstance().getKitHandler().getDefaultKit();
                        Kit previousKit = playerData.getPreviousKit();

                        if (previousKit != null && playerData.hasKit(previousKit)) {
                            selectedKit = previousKit;
                        }

                        selectedKit.apply(player, true, true);
                    }

                    player.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
                    playerData.setNoFallDamage(true);
                }

            } else {

                if (!RegionType.SAFEZONE.appliesTo(from) && RegionType.SAFEZONE.appliesTo(to)) {
                    player.teleport(from);
                    player.setVelocity(from.toVector().subtract(to.toVector()).normalize().multiply(1.25).add(new Vector(0, 0.5, 0)).setY(.1));
                }

            }
        }

        Location baseLocation = to.clone().subtract(0, .01D, 0);

        if(baseLocation.getBlock().getType() == Material.SPONGE) {
            Vector send = new Vector(0, 0, 0);

            if(baseLocation.subtract(0, 1, 0).getBlock().getType() == Material.SPONGE) {
                for (SpongeFaces face : SpongeFaces.values()) {
                    Location curLocation = baseLocation.clone().add(face.getXOffset(), 0, face.getZOffset());
                    int tri = 0;

                    while (tri < 10 && curLocation.getBlock().getType() == Material.SPONGE) {
                        curLocation.add(face.getXOffset(), face.getYOffset(), face.getZOffset());
                        face.addToVector(send, .75);
                        tri++;
                    }
                }
                player.setVelocity(send);
            }
        }

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event.getPlayer(), event.getTo(), event.getFrom());
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {

            Location to = event.getTo();
            Location from = event.getFrom();

            Player player = event.getPlayer();
            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            if (!playerData.isSpawnProtection()) {
                if (!RegionType.SAFEZONE.appliesTo(from) && RegionType.SAFEZONE.appliesTo(to)) {
                    if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                        player.sendMessage(ChatColor.RED + "You cannot enderpearl into spawn, used enderpearl has been refunded.");
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        player.updateInventory();
                    }

                    event.setTo(event.getFrom());
                    event.setCancelled(true);
                    player.setVelocity(from.toVector().subtract(to.toVector()).normalize().multiply(1.25).add(new Vector(0, 0.5, 0)).setY(.1));
                }
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum SpongeFaces {

        NORTH(0, 0, -1) {
            @Override
            public void addToVector(Vector send, double mult) {
                MovementListener.addToVector(0, 0, mult, send);
            }
        },
        EAST(1, 0, 0){
            @Override
            public void addToVector(Vector send, double mult) {
                MovementListener.addToVector(mult, 0, 0, send);
            }
        },
        SOUTH(0, 0, 1){
            @Override
            public void addToVector(Vector send, double mult) {
                MovementListener.addToVector(0, 0, mult, send);
            }
        },
        WEST(-1, 0, 0){
            @Override
            public void addToVector(Vector send, double mult) {
                MovementListener.addToVector(mult, 0, 0, send);
            }
        },

        DOWN(0, -1, 0) {
            @Override
            public void addToVector(Vector send, double mult) {
                MovementListener.addToVector(0, mult, 0, send);
            }
        };


        private int xOffset, yOffset, zOffset;

        public abstract void addToVector(Vector send, double mult);

    }

    public static void addToVector(double x, double y, double z, Vector vector) {
        vector.setX(vector.getX() + x);
        vector.setY(vector.getY() + y);
        vector.setZ(vector.getZ() + z);
    }
}

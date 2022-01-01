package rip.thecraft.brawl.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArena;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.MatchState;
import rip.thecraft.brawl.spawn.event.Event;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameElimination;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.team.GamePlayer;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.server.region.RegionType;
import rip.thecraft.brawl.spectator.SpectatorMode;
import rip.thecraft.falcon.staff.StaffMode;
import rip.thecraft.server.handler.MovementHandler;

@RequiredArgsConstructor
public class MovementListener implements MovementHandler, Listener {

    private final Brawl plugin;

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

            if (to.getY() < 0 && !player.isDead()) {
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
                if (gamePlayer.isAlive()) {
                    game.processMovement(player, gamePlayer, from, to);
                    if (game.getFlags().contains(GameFlag.WATER_ELIMINATE)) {
                        if (to.getBlock().isLiquid()) {
                            game.handleElimination(player, to, GameElimination.WATER);
                        }
                    }
                }
            }


            if (playerData.isDuelArena() && playerData.isSpawnProtection()) {
                playerData.setSpawnProtection(false);
            }

            if (StaffMode.hasStaffMode(player)) return;

            if (playerData.isSpectating()) {
                SpectatorMode spectatorMode = plugin.getSpectatorManager().getSpectator(player);
                int maxRadius = spectatorMode.getMaxRadius();
                Location teleportTo = spectatorMode.getTeleportTo();
                if (teleportTo != null && maxRadius > 0) {
                    double distance = to.distance(teleportTo);
                    if (distance > maxRadius) {
                        player.teleport(teleportTo);
                        player.sendMessage(ChatColor.RED + "You can't go more than " + maxRadius + " blocks from your Spectating point.");
                    }
                }
                return;
            }

            if (player.getGameMode() == GameMode.CREATIVE) return;

            if (playerData.isSpawnProtection()) {
                if (RegionType.SAFEZONE.appliesTo(from) && !RegionType.SAFEZONE.appliesTo(to)) {
                    playerData.setSpawnProtection(false);
                    Event activeEvent = Brawl.getInstance().getEventHandler().getActiveEvent();
                    if (activeEvent != null) {
                        activeEvent.onSpawnLeave(player, playerData);
                    }
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
                    player.eject();
                    player.setVelocity(from.toVector().subtract(to.toVector()).normalize().multiply(1.25).add(new Vector(0, 0.5, 0)).setY(.1));
                }
            }

            Location baseLocation = to.clone().subtract(0, .01D, 0);

            if (baseLocation.getBlock().getType() == Material.SPONGE) {
                Vector send = new Vector(0, 0, 0);

                if (baseLocation.subtract(0, 1, 0).getBlock().getType() == Material.SPONGE) {
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
            } else if (baseLocation.getBlock().getType() == Material.GOLD_BLOCK) {
                Block relative = baseLocation.getBlock().getRelative(BlockFace.DOWN);
                if (relative.getType() == Material.COMMAND) {
                    CommandBlock commandBlock = (CommandBlock) relative.getState();
                    String command = commandBlock.getCommand();
                    if (!command.startsWith("/") && command.contains(";")) {
                        String[] locations = command.split(";");
                        String location = locations[Brawl.RANDOM.nextInt(locations.length)];
                        if (playerData.getLastAction() < System.currentTimeMillis()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tp " + player.getName() + " " + location);
                            playerData.setLastAction(System.currentTimeMillis() + 500L);
                            player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                            player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
                        }
                    }
                }
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

            if (playerData.isDuelArena() && RegionType.SAFEZONE.appliesTo(to)) {
                DuelArena.leave(player);
            }
        }
    }

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        onPlayerMove(player, to, from);
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

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

package rip.thecraft.brawl.ability;

import org.bukkit.event.Listener;

public final class AbilityListener implements Listener {

//    private final Map<UUID, Long> abilityCooldown = new ConcurrentHashMap<>();
//
//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onProjectileLaunch(ProjectileLaunchEvent event) {
//        if (event.getEntityType() != EntityType.ENDER_PEARL) {
//            return;
//        }
//
//        EnderPearl pearl = (EnderPearl) event.getEntity();
//        Player shooter = (Player) pearl.getShooter();
//
//        abilityCooldown.put(shooter.getUniqueId(), System.currentTimeMillis() + PEARL_COOLDOWN_MILLIS);
//
//        // cannot be made a lambda because of cancel() usage
//        new BukkitRunnable() {
//
//            public void run() {
//                long cooldownExpires = pearlCooldown.getOrDefault(shooter.getUniqueId(), 0L);
//
//                if (cooldownExpires < System.currentTimeMillis()) {
//                    cancel();
//                    return;
//                }
//
//                int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
//                float percentLeft = (float) millisLeft / PEARL_COOLDOWN_MILLIS;
//
//                shooter.setExp(percentLeft);
//                shooter.setLevel(millisLeft / 1_000);
//            }
//
//        }.runTaskTimer(PotPvPSI.getInstance(), 1L, 1L);
//    }
//
//    @EventHandler
//    public void onTeleport(PlayerTeleportEvent event) {
//        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
//            event.setTo(event.getTo().add(0.5, 0, 0.5));
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL || !event.getAction().name().contains("RIGHT_")) {
//            return;
//        }
//
//        Player player = event.getPlayer();
//        long cooldownExpires = pearlCooldown.getOrDefault(player.getUniqueId(), 0L);
//
//        if (cooldownExpires < System.currentTimeMillis()) {
//            return;
//        }
//
//        int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
//        double secondsLeft = millisLeft / 1000D;
//        // round to 1 digit past decimal
//        secondsLeft = Math.round(10D * secondsLeft) / 10D;
//
//        event.setCancelled(true);
//        player.sendMessage(ChatColor.BLUE + "Cooldown) " + ChatColor.GRAY + "You cannot use " + ChatColor.GREEN + "Enderpearl" + ChatColor.GRAY + " for " + ChatColor.GREEN + secondsLeft + ChatColor.GREEN + " seconds" + ChatColor.GRAY + ".");
//        player.updateInventory();
//    }
//
//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        pearlCooldown.remove(event.getPlayer().getUniqueId());
//    }
//
//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent event) {
//        Player player = event.getEntity();
//
//        // When players die, their enderpearls are still left on the map,
//        // allowing players to teleport after they die
//        for (EnderPearl pearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
//            if (pearl.getShooter() == player) {
//                pearl.remove();
//            }
//        }
//
//        pearlCooldown.remove(player.getUniqueId());
//    }
//
//    // reset pearl cooldowns when ending a match
//    // this is only so (most) players don't see the cooldown
//    // in the lobby - the 'actual' reset is the one prior to
//    // start a match, as with this we can 'forget' players who
//    // died (and aren't alive anymore) right before the end of
//    // a match.
//    @EventHandler
//    public void onMatchTerminate(MatchTerminateEvent event) {
//        for (MatchTeam team : event.getMatch().getTeams()) {
//            team.getAliveMembers().forEach(pearlCooldown::remove);
//        }
//    }
//
//    // see comment on #onMatchTerminate(MatchTerminateEvent)
//    @EventHandler
//    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
//        for (MatchTeam team : event.getMatch().getTeams()) {
//            team.getAllMembers().forEach(pearlCooldown::remove);
//        }
//    }

}
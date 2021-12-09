package rip.thecraft.brawl.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.MatchState;
import rip.thecraft.brawl.duelarena.match.data.MatchData;
import rip.thecraft.brawl.item.type.MetadataType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.HiddenStringUtils;
import rip.thecraft.falcon.rank.Rank;

import java.util.List;

@RequiredArgsConstructor
public class ArenaListener implements Listener {

    private final Brawl plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (Match match : plugin.getMatchHandler().getMatches()) {
            for (Player member : match.getPlayers()) {
                if (member == null || match.getQuited() == member.getUniqueId() || match.isQuitEnded() || match.getState() == MatchState.FINISHED) continue;
                member.hidePlayer(event.getPlayer());
                if (match.getArena().getArenaType() != ArenaType.NORMAL) {
                    event.getPlayer().hidePlayer(member);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        DuelArenaHandler mh = plugin.getMatchHandler();
        Match match = mh.getMatch(player);
        if (match != null) {
            match.quit(event.getPlayer());
        }
        mh.cleanup(player.getUniqueId());
        mh.refreshQuickqueue();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase().split(" ")[0].replace("/", "");

        if (!player.hasPermission(Rank.STAFF_NODE) && plugin.getMatchHandler().isInMatch(player) && !plugin.getConfig().getStringList("ALLOWED_COMMANDS").contains(command)) {
            player.sendMessage(ChatColor.RED + "You cannot execute this command while in a match.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                Match match = plugin.getMatchHandler().getMatch(player);
                if (match != null) {
                    MatchData matchData = match.getMatchData();
                    matchData.addHits(player);
                    matchData.setLastHit(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            ItemStack item = player.getItemInHand();
            if (item != null && item.hasItemMeta()) {
                List<String> lore = item.getLore();
                if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                    String metaData = HiddenStringUtils.extractHiddenString(lore.get(0));
                    if (MetadataType.isMetadata(metaData)) {
                        MetadataType metadataType = MetadataType.fromMetadata(metaData);
                        if (metadataType.isCancellable()) {
                            event.setCancelled(true);
                        }

                        if (metadataType == MetadataType.DUEL_ARENA_DUEL) {
                            player.chat("/duel " + target.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();
        Match match = plugin.getMatchHandler().getMatch(player);
        if (match != null && match.contains(player)) {
            Projectile entity = event.getEntity();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (match.contains(p) || match.getMatchData().getSpectators().contains(p.getUniqueId())) continue;
                plugin.getEntityHider().hideEntity(p, entity);
            }
        }

    }

    @EventHandler
    public void onThrow(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();
        Match match = plugin.getMatchHandler().getMatch(player);
        if (match != null && match.contains(player)) {
            for (Entity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {

                    Player player2 = (Player) entity;
                    if (!match.contains(player2)) {
                        event.setIntensity((LivingEntity) entity, 0);
                    }

                } else {
                    event.setIntensity((LivingEntity) entity, 0);
                }
            }
        }
    }

    @EventHandler
    public void onEntitytDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("NPC")) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

            // todo add check qif playuer is in match
            boolean inMatch = plugin.getMatchHandler().isInMatch(player);
            if (inMatch) {
                Match match = plugin.getMatchHandler().getMatch(player);
                if (match.getState() != MatchState.FIGHTING) {
                    event.setCancelled(true);
                } else if (match.getArena().getArenaType() == ArenaType.SUMO) {
                    event.setDamage(0.0);
                }
            } else if (playerData.isDuelArena()) {
                event.setCancelled(true);
            }
        }
    }

}

package rip.thecraft.brawl.player.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.killstreak.Killstreak;
import rip.thecraft.brawl.killstreak.KillstreakHandler;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.levels.ExperienceType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.*;
import java.util.stream.DoubleStream;

@Getter
@RequiredArgsConstructor
public class SpawnData {

    public static final double MIN_PERCENT_FOR_ASSIST = .75;

    private final PlayerData playerData;

    private Map<UUID, Double> damageReceived = new HashMap<>();

    public List<String> applyAssists(Player killer, double finalCredits) {
        List<String> assisters = new ArrayList<>();
        for (UUID uuid : damageReceived.keySet()) {
            Player damager = Bukkit.getPlayer(uuid);

            if (damager != null && damager != killer) {
                PlayerData damagerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(damager);
                double pct = getPctDamaged(damager);
                if (pct >= MIN_PERCENT_FOR_ASSIST) {
                    double tokens = pct * finalCredits;
                    for (PlayerChallenge challenge : damagerData.getChallengeTracker().getChallenges().values()) {
                        if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.CREDITS) {
                            challenge.increment(damager, Math.round((float) tokens));
                        }
                    }
                    playerData.getLevel().addExp(damager, ExperienceType.KILL_ASSISTS, killer.getName());
                    playerData.fetchPlayer().ifPresent(player -> player.sendMessage(CC.DARK_PURPLE + "You've earned " + CC.LIGHT_PURPLE + Math.round(tokens) + " credits" + CC.DARK_PURPLE + " for dealing " + CC.LIGHT_PURPLE + (Math.round(tokens * 10) / 10.0) + "%" + CC.DARK_PURPLE + " damage to " + CC.WHITE + killer.getDisplayName() + CC.DARK_PURPLE + "."));
                    assisters.add(damager.getName());
                }
            }
        }
        return assisters;
    }

    private static long COMBAT_TAG_TIMER = 15;
    public void damagedBy(Player damager, double damage) {
        UUID uuid;
        if (damager == null) {
            uuid = new UUID(420, 420);
        } else {
            uuid = damager.getUniqueId();
        }

        damageReceived.putIfAbsent(uuid, 0D);
        damageReceived.put(uuid, damageReceived.get(uuid) + damage);

        long combatTimer = System.currentTimeMillis() + (1000 * COMBAT_TAG_TIMER);
        playerData.setCombatTaggedTil(combatTimer);
        if (damager != null) {
            Brawl.getInstance().getPlayerDataHandler().getPlayerData(damager).setCombatTaggedTil(combatTimer);
        }
    }

    public double getPctDamaged(Player damager) {
        double total = damageReceived.values().stream().flatMapToDouble(DoubleStream::of).sum();
        double dealt = damageReceived.get(damager.getUniqueId());

        return total == 0 ? 0 : dealt / total;
    }

    public double getWorth() {
        double base = 15;

        Kit selectedKit = playerData.getSelectedKit();
        base += selectedKit != null && !selectedKit.isFree() ? 5 : 0;
        base += playerData.getStatistic().get(StatisticType.KILLSTREAK);

        base = Math.min(30, base);
        return base;
    }

    public boolean killed(Player dead) {
        boolean ksEnded = false;

        PlayerStatistic stats = playerData.getStatistic();

        stats.add(StatisticType.KILLS);
        stats.add(StatisticType.KILLSTREAK);

        PlayerData deadPlayer = Brawl.getInstance().getPlayerDataHandler().getPlayerData(dead.getUniqueId());
        double worth = deadPlayer.getSpawnData().getWorth();
        Player player = playerData.getPlayer();

        if (playerData.usingPerk(Perk.REVENGE)) {
            stats.add(StatisticType.CREDITS, worth * 2);
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> player.sendMessage(CC.DARK_PURPLE + "You have killed " + CC.WHITE + dead.getDisplayName() + CC.DARK_PURPLE + " for " + CC.LIGHT_PURPLE + Math.round(worth * 2) + " credits" + CC.DARK_PURPLE + ". " + CC.GRAY + "(Doubled by Revenge perk)"), 1L);
        } else {
            stats.add(StatisticType.CREDITS, worth);
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> player.sendMessage(CC.DARK_PURPLE + "You have killed " + CC.WHITE + dead.getDisplayName() + CC.DARK_PURPLE + " for " + CC.LIGHT_PURPLE + Math.round(worth) + " credits" + CC.DARK_PURPLE + "."), 1L);
        }

        if (deadPlayer.getStatistic().get(StatisticType.KILLSTREAK) >= 10) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerData onlineData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(online);
                if (onlineData.isKillstreakMessages()) {
                    online.sendMessage(CC.WHITE + player.getDisplayName() + CC.YELLOW + " has ended " + CC.WHITE + dead.getDisplayName() + CC.YELLOW + " killstreak of " + CC.LIGHT_PURPLE + deadPlayer.getStatistic().get(StatisticType.KILLSTREAK) + CC.YELLOW + "!");
                }
            }

            ksEnded = true;

        }

        if (stats.get(StatisticType.KILLSTREAK) > stats.get(StatisticType.HIGHEST_KILLSTREAK)) {
            stats.set(StatisticType.HIGHEST_KILLSTREAK, stats.get(StatisticType.KILLSTREAK));
        }

        if (playerData.getSelectedKit() != null) {
            KitStatistic ks = stats.get(playerData.getSelectedKit());
            ks.setKills(ks.getKills() + 1);
        }

        int streak = (int) stats.get(StatisticType.KILLSTREAK);
        KillstreakHandler handler = Brawl.getInstance().getKillstreakHandler();

        if (handler.getStreaks().containsKey(streak)) {
            Killstreak killstreak = handler.getStreaks().get(streak);
            killstreak.onKill(player, playerData);
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerData onlineData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(online);
                if (onlineData.isKillstreakMessages()) {
                    online.sendMessage(CC.WHITE + player.getDisplayName() + CC.YELLOW + " has gotten a killstreak of " + CC.LIGHT_PURPLE + (int) stats.get(StatisticType.KILLSTREAK) + CC.YELLOW + " and received " + killstreak.getColor() + killstreak.getName() + CC.YELLOW + ".");
                }
            }
        }

        for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
            if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.KILLS) {
                challenge.increment(player, 1);
            }
        }

//        if ((stats.get(StatisticType.KILLSTREAK) > 15 && stats.get(StatisticType.KILLSTREAK) % 10 == 0) || (stats.get(StatisticType.KILLSTREAK) == 5 && stats.get(StatisticType.KILLSTREAK) <= 15)) {
//            for (Player online : Bukkit.getOnlinePlayers()) {
//                String killstreakName = "ADD KILLSTREAKS";
//                online.sendMessage(CC.WHITE + player.getDisplayName() + CC.YELLOW + " has gotten a killstreak of " + CC.LIGHT_PURPLE + (int)stats.get(StatisticType.KILLSTREAK) + CC.YELLOW + " and received " + killstreakName + CC.YELLOW + ".");
//            }
//            // TODO REWARD KILLSTREAKS
//
//        }
        return ksEnded;
    }


}

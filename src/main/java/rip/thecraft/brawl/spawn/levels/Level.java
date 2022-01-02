package rip.thecraft.brawl.spawn.levels;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.spawn.challenges.ChallengeType;
import rip.thecraft.brawl.spawn.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.util.MathUtil;
import rip.thecraft.server.util.chatcolor.CC;

@Getter
@Setter
@RequiredArgsConstructor
public class Level {

    private static final double[] PRESTIGE_XP_MULTIPLIERS = {
            1, 1.1, 1.2, 1.3, 1.4, 1.5, 1.75, 2, 2.5, 3, 4, 5, 6, 7, 8, 9, 10,
            12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 10
    };

    public static final int MAX_LEVEL = 100;

    private final PlayerData playerData;

    private int currentExp = 0;

    public boolean canPrestige() {
        return getLevel() >= MAX_LEVEL && currentExp >= getMaxExperience();
    }

    public int getPrestige() {
        return (int) playerData.getStatistic().get(StatisticType.PRESTIGE);
    }

    public int getLevel() {
        return (int) playerData.getStatistic().get(StatisticType.LEVEL);
    }

    public double getPrestigeXpMultiplier() {
        return PRESTIGE_XP_MULTIPLIERS[getPrestige()];
    }

    public boolean isPrestige() {
        return getPrestige() >= 1;
    }

    public int getMaxExperience() {
        return (int) (Levels.getByLevel(getCurrentLevel()).getExperience() * getPrestigeXpMultiplier());
    }

    public double getPercentageExp() {
        return MathUtil.getPercent(currentExp, getMaxExperience());
    }

    public void addExp(Player player, int exp, ExperienceType type, Object... args) {
        playerData.getStatistic().add(StatisticType.TOTAL_EXPERIENCE, exp);
        currentExp += exp;

        if (player != null) {
            player.setExp((float) (getPercentageExp() * 0.01F));
            String message = ChatColor.GREEN + "+" + exp + " exp";
            if (type != null) {
                message += ChatColor.GRAY + " (" + String.format(type.getName(), args) + ChatColor.GRAY + ")";
            }
            player.sendMessage(message);
        }

        for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
            if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.EXPERIENCE) {
                challenge.increment(player, exp);
            }
        }

        Kit unlockingKit = playerData.getUnlockingKit();
        if (unlockingKit != null) {
            KitStatistic kitStatistic = playerData.getStatistic().get(unlockingKit);
            int newExp = kitStatistic.getExp() + exp;
            if (newExp >= Kit.MAX_EXP_UNLOCK) {
                kitStatistic.setExp(0);
                playerData.setUnlockingKit(null);
                playerData.addUnlockedKit(unlockingKit);
            } else {
                kitStatistic.setExp(newExp);
            }
        }

        if (getCurrentLevel() >= MAX_LEVEL) {
            if (player != null) {
                if (currentExp < getMaxExperience()) {
                    player.sendMessage(ChatColor.RED + "You have reached the highest level! Type /prestige to advance.");
                }
            }
            return;
        }

        while (currentExp >= getMaxExperience()) {
            addLevel(player);
        }
    }

    public void addExp(Player player, ExperienceType type, Object... args) {
        addExp(player, type.getExperience(), type, args);
    }

    @Deprecated
    public void addExp(Player player, int exp, String action) {
        playerData.getStatistic().add(StatisticType.TOTAL_EXPERIENCE, exp);
        currentExp += exp;

        if (player != null) {
            if (playerData.isSpawnProtection() || playerData.isDuelArena()) {
                player.setExp((float) (getPercentageExp() * 0.01F));
            }
            String message = ChatColor.GREEN + "+" + exp + " exp";
            if (action != null) {
                message += ChatColor.GRAY + " (" + action + ChatColor.GRAY + ")";
            }
            player.sendMessage(message);
        }

        for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
            if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.EXPERIENCE) {
                challenge.increment(player, exp);
            }
        }

        Kit unlockingKit = playerData.getUnlockingKit();
        if (unlockingKit != null) {
            KitStatistic kitStatistic = playerData.getStatistic().get(unlockingKit);
            int newExp = kitStatistic.getExp() + exp;
            if (newExp >= Kit.MAX_EXP_UNLOCK) {
                kitStatistic.setExp(0);
                playerData.setUnlockingKit(null);
                playerData.addUnlockedKit(unlockingKit);
            } else {
                kitStatistic.setExp(newExp);
            }
        }

        if (getCurrentLevel() >= MAX_LEVEL) {
            if (player != null) {
                if (currentExp < getMaxExperience()) {
                    player.sendMessage(ChatColor.RED + "You have reached the highest level! Type /prestige to advance.");
                }
            }
            return;
        }

        while (currentExp >= getMaxExperience()) {
            addLevel(player);
        }
    }

    public void addLevel(Player player) {
        currentExp -= getMaxExperience();
        int level = (int) playerData.getStatistic().add(StatisticType.LEVEL);
        Levels levelData = Levels.getByLevel(level);
        if (player != null) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "LEVEL UNLOCKED! " + ChatColor.GRAY + "You've ranked up to level " + levelData.getColor() + level + ChatColor.GRAY + "!");
            for (LevelFeature feature : levelData.getFeatures()) {
                player.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + feature.getName());
            }
//            new LevelFlashTask(player, this).runTaskTimer(Brawl.getInstance(), 0, 7); // Run cool animation :D
        }
        playerData.markForSave();
    }

    public int getCurrentLevel() {
        return (int) playerData.getStatistic().get(StatisticType.LEVEL);
    }

    public void updateExp(Player player) {
        if (player != null) {
            if (playerData.isSpawnProtection() || playerData.isDuelArena()) {
                player.setLevel(getCurrentLevel());
                player.setExp((float) (getPercentageExp() * 0.01F));
            }
        }
    }

    public void load(Document document) {
        this.currentExp = document.getInteger("exp", 0);
        updateExp(getPlayerData().getPlayer());
    }

    public Document toDocument() {
        return new Document("exp", currentExp);
    }

    public String getPrefix() {
        return Levels.getPrefix(this) + " ";
    }

    public String getDisplayName() {
        return Levels.getPrefix(this).replace("[", "").replace("]", "");
    }

    public String getSimplePrefix() {
        return CC.strip(Levels.getPrefix(this));
    }
}

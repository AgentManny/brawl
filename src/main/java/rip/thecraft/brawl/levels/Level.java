package rip.thecraft.brawl.levels;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.levels.task.LevelFlashTask;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.util.MathUtil;

@Getter
@RequiredArgsConstructor
public class Level {

    public static final int BASE_EXPERIENCE = 25;

    private final PlayerData playerData;

    @Setter private int currentExp = 0;

    public int getMaxExperience() {
        return getCurrentLevel() * BASE_EXPERIENCE;
    }

    public double getPercentage() {
        return MathUtil.getPercent(currentExp, getMaxExperience());
    }

    //float percent = (float)((Utils.getPercent(this.getXP(), Utils.getLevelRequiredXP(this.getLevel() + 1))) * (0.01F));
    public void addExp(Player player, int exp, String action) {
        if (player != null) {
            player.setExp((float) (getPercentage() * 0.01F));
            player.sendMessage(ChatColor.GREEN + "+" + exp + " exp" + ChatColor.GRAY + " (" + action + ChatColor.GRAY + ")");
        }
        currentExp += exp;
        while (currentExp >= getMaxExperience()) {
            addLevel(player);
        }
    }

    public void addLevel(Player player) {
        currentExp -= getMaxExperience();
        playerData.getStatistic().add(StatisticType.LEVEL);

        if (player != null) {
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "LEVEL UNLOCKED! " + ChatColor.GRAY + "You've ranked up to level " + ChatColor.GREEN + getCurrentLevel() + ChatColor.GRAY + "!");
            new LevelFlashTask(player, this).runTaskTimer(Brawl.getInstance(), 0, 7); // Run cool animation :D
        }
        playerData.markForSave();
    }

    public int getCurrentLevel() {
        return (int) Math.max(1, playerData.getStatistic().get(StatisticType.LEVEL));
    }

    public void updateExp(Player player) {
        if (player != null) {
            player.setLevel(getCurrentLevel());
            player.setExp((float) (getPercentage() * 0.01F));
        }
    }

    public void load(Document document) {
        this.currentExp = document.getInteger("exp", 0);
        updateExp(getPlayerData().getPlayer());
    }

    public Document toDocument() {
        return new Document("exp", currentExp);
    }

    public static String getColor(int level) {
        ChatColor color;
        if (level < 5) {
            color = ChatColor.GRAY;
        } else if (level < 10) {
            color = ChatColor.WHITE;
        } else if (level < 15) {
            color = ChatColor.GOLD;
        } else if (level < 20) {
            color = ChatColor.AQUA;
        } else if (level < 25) {
            color = ChatColor.DARK_AQUA;
        } else if (level < 50) {
            color = ChatColor.LIGHT_PURPLE;
        } else if (level < 75) {
            color = ChatColor.DARK_PURPLE;
        } else {
            color = ChatColor.WHITE;
        }
        //✫

        return color.toString();
    }

    public String getPrefix() {
        return getColor(getCurrentLevel()) + "[" + getCurrentLevel() + "✫] ";
    }

    public String getSimplePrefix() {
        return getColor(getCurrentLevel()) + "[" + getCurrentLevel() + "] ";
    }


}
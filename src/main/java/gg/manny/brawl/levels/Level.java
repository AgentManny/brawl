package gg.manny.brawl.levels;

import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Level {

    public static final int BASE_EXPERIENCE = 15;

    private final PlayerData playerData;

    private int currentExp = 0;

    public int getMaxExperience() {
        return getCurrentLevel() * BASE_EXPERIENCE;
    }

    public void addExp(Player player, int exp, String action) {
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "+" + exp + " exp" + CC.GRAY + " (" + action + CC.GRAY + ")");
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
            player.sendMessage(CC.GREEN + CC.BOLD + "LEVEL UNLOCKED! " + CC.GRAY + "You've ranked up to level " + CC.GREEN + getCurrentLevel() + CC.GRAY + "!");
        }
        playerData.markForSave();
    }

    public int getCurrentLevel() {
        return (int) Math.max(1, playerData.getStatistic().get(StatisticType.LEVEL));
    }

    public void load(Document document) {
        this.currentExp = document.getInteger("exp", 0);
    }

    public Document toDocument() {
        return new Document("exp", currentExp);
    }

    public static String getColor(int level) {
        String color;
        if (level < 5) {
            color = CC.GRAY;
        } else if (level < 10) {
            color = CC.WHITE;
        } else if (level < 15) {
            color = CC.GOLD;
        } else if (level < 20) {
            color = CC.AQUA;
        } else if (level < 25) {
            color = CC.DARK_AQUA;
        } else if (level < 50) {
            color = CC.LIGHT_PURPLE;
        } else if (level < 75) {
            color = CC.DARK_PURPLE;
        } else {
            color = CC.WHITE;
        }
        //✫

        return color;
    }

    public String getPrefix() {
        return getColor(getCurrentLevel()) + "[" + getCurrentLevel() + "✫] ";
    }

    public String getSimplePrefix() {
        return getColor(getCurrentLevel()) + "[" + getCurrentLevel() + "] ";
    }


}

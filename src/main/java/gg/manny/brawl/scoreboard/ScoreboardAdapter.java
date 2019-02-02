package gg.manny.brawl.scoreboard;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.construct.ConstructAdapter;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ScoreboardAdapter implements ConstructAdapter {

    private final Brawl plugin;

    @Override
    public String getTitle(Player player) {
        return Locale.SCOREBOARD_SPAWN_TITLE.format();
    }

    @Override
    public List<String> getLines(Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);

        List<String> lines = Locale.SCOREBOARD_SPAWN_LINES.toList();
        List<String> toReturn = new ArrayList<>();
        for(String line : lines) {
            String newLine = line.replace("{LINE}", CC.SCOREBAORD_SEPARATOR);

            for(StatisticType statisticType : StatisticType.values()) {
                newLine = newLine
                        .replace("{" + statisticType.name() + "}", playerData.getStatistic().get(statisticType) + "")
                        .replace("{" + statisticType.name() + ":ROUNDED}", (int) Math.round(playerData.getStatistic().get(statisticType)) + "");
            }

            if(newLine.contains("{KIT}")) {
                if (playerData.getSelectedKit() == null) continue;
                newLine = newLine.replace("{KIT}", playerData.getSelectedKit().getName());
            }

            toReturn.add(newLine);
        }

        return toReturn;
    }
}

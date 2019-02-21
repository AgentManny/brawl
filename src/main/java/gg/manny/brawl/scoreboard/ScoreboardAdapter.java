package gg.manny.brawl.scoreboard;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.ability.Ability;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.player.statistic.StatisticType;
import gg.manny.brawl.util.DurationFormatter;
import gg.manny.construct.ConstructAdapter;
import gg.manny.pivot.util.Cooldown;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Kit kit = playerData.getSelectedKit();
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
                if (kit == null) continue;
                newLine = newLine.replace("{KIT}", playerData.getSelectedKit().getName());
            }

            if (newLine.contains("{ABILITY:ACTIVE:KEY}")) {
                if (kit != null) {
                    for (Ability ability : kit.getAbilities()) {
                        Map<String, String> properties = ability.getProperties(player);
                        for (Map.Entry<String, String> property : properties.entrySet()) {
                            toReturn.add(newLine
                                    .replace("{ABILITY:ACTIVE:KEY}", property.getKey())
                                    .replace("{ABILITY:ACTIVE:VALUE}", property.getValue()));
                        }
                    }
                }
                continue;
            }
            if (newLine.contains("{ABILITY}")) {
                if (playerData.hasCooldown("ENDERPEARL")) {
                    Cooldown cooldown = playerData.getCooldown("ENDERPEARL");
                    toReturn.add(newLine
                            .replace("{ABILITY}", "Enderpearl")
                            .replace("{ABILITY:TIME}", DurationFormatter.getRemaining(cooldown.getRemaining())));
                }
                if (kit != null) {
                    for (Ability ability : kit.getAbilities()) {
                        if (ability.hasCooldown(player, false)) {
                            toReturn.add(newLine
                            .replace("{ABILITY}", ability.getName())
                            .replace("{ABILITY:TIME}", DurationFormatter.getRemaining(ability.toCooldown(playerData).getRemaining())));
                        }
                    }
                }
                continue;
            }


            toReturn.add(newLine);
        }

        return toReturn;
    }
}

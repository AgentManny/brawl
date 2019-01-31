package gg.manny.brawl.player.statistic;

import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor
public enum StatisticType {

    COINS("Coins"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    KILLSTREAK("Killstreak"),
    HIGHEST_KILLSTREAK("Highest Killstreak"),
    KDR("KDR");

    private final String name;

    public static StatisticType parse(String input) {
        return Stream.of(values()).filter(s ->
                s.name().equalsIgnoreCase(input)
                        || s.name.replace(" ", "").equalsIgnoreCase(input.replace(" ", ""))
        ).map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);
    }
}

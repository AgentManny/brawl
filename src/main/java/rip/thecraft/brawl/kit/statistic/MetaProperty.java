package rip.thecraft.brawl.kit.statistic;

import com.google.common.base.Function;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaProperty {

    private Kit kit;

    private String key;
    private double def;

    public double get(PlayerData playerData) {
        return (double) playerData.getStatistic().get(kit).getProperties().getOrDefault(key, def);
    }

    public void set(PlayerData playerData, double data) {
        playerData.getStatistic().get(kit).getProperties().put(key, data);
    }

    public void mod(PlayerData playerData, Function<Double, Double> func) {
        set(playerData, func.apply(get(playerData)));
    }

}

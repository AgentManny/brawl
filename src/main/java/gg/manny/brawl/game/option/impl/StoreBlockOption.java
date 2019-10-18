package gg.manny.brawl.game.option.impl;

import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.option.GameOption;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class StoreBlockOption implements GameOption {

    @NonNull
    private final List<Material> allowedBlocks;
    private Map<Location, BlockState> data = new HashMap<>();

    private Set<Material> pickable = EnumSet.noneOf(Material.class);
    private int randomRange = 1;

    @Override
    public void onEnd(Game game) {
        data.forEach((location, state) -> {
            location.getBlock().setType(state.getType());
            location.getBlock().setData(state.getData().getData());
        });
    }

    public StoreBlockOption materials(Material... materials) {
        pickable.addAll(Arrays.asList(materials));
        return this;
    }

    public StoreBlockOption range(int range) {
        this.randomRange = range;
        return this;
    }
}

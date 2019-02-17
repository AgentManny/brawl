package gg.manny.brawl.game.option.impl;

import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.option.GameOption;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class StoreBlockOption implements GameOption {

    @Getter
    @NonNull
    private final List<Material> allowedBlocks;

    @Getter
    private Map<Location, BlockState> data = new HashMap<>();

    @Override
    public void onEnd(Game game) {
        data.forEach((location, state) -> {
            location.getBlock().setType(state.getType());
            location.getBlock().setData(state.getData().getData());
        });
    }

}

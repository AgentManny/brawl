package rip.thecraft.brawl.game.games;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.kit.Kit;

public class FFA extends Game {

    private Kit defaultKit;

    public FFA() {
        super(GameType.FFA, GameFlag.PLAYER_ELIMINATE);
        defaultKit = Brawl.getInstance().getKitHandler().getDefaultKit();
    }

    @Override
    public void addItems(Player player) {
        defaultKit.apply(player, false, false);
    }
}

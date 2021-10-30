package rip.thecraft.brawl.game.games;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;

public class FFA extends Game {

    public FFA() {
        super(GameType.FFA, GameFlag.PLAYER_ELIMINATE);
    }

    @Override
    public void addItems(Player player) {
        Brawl.getInstance().getKitHandler().getDefaultKit().apply(player, false, true);
    }
}

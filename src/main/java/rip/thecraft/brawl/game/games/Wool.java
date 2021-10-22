package rip.thecraft.brawl.game.games;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;

/**
 * Created by Flatfile on 10/22/2021.
 */
public class Wool extends Game {

    public Wool(){
        super(GameType.WOOL, GameFlag.PLAYER_ELIMINATE);
    }

    @Override
    public void setup() {
        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getLocationByName("Lobby"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 2));
        });

        this.startTimer(5, true);
    }

}

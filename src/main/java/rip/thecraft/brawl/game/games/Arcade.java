package rip.thecraft.brawl.game.games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.PlayerUtil;

import java.util.List;
import java.util.Random;

//TODO scoreboard, shuffle countdown
/**
 * Created by Flatfile on 10/21/2021.
 */
public class Arcade extends Game {

    public Random random = new Random();
    public List<Kit> kits;

    public BukkitTask task;

    public Arcade(){
        super(GameType.ARCADE, GameFlag.PLAYER_ELIMINATE);
    }

    @Override
    public void setup() {
        this.kits = Brawl.getInstance().getKitHandler().getKits();

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getLocationByName("Lobby"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 2));
        });

        this.startTimer(5, true);
        task = Bukkit.getScheduler().runTaskTimer(Brawl.getInstance(), this::randomizeKits, 6*20, 30*20);
    }

    @Override
    public void end() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        clean();
        super.end();
    }

    public void clean(){
        getPlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            data.setPreviousKit(data.getSelectedKit());
            data.setSelectedKit(null);
            PlayerUtil.resetInventory(player);
        });
    }

    public void applyRandomKit(Player player){
        int kitCount = kits.size();
        int rand = random.nextInt(kitCount);
        Kit chosenKit = kits.get(rand);

        chosenKit.apply(player, true, true);
    }

    public void randomizeKits(){
        getAlivePlayers().forEach(player -> applyRandomKit(player.toPlayer()));
    }

}

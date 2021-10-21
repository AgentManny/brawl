package rip.thecraft.brawl.game.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.game.Game;
import rip.thecraft.brawl.game.GameFlag;
import rip.thecraft.brawl.game.GameState;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
/**
 * Created by Flatfile on 10/21/2021.
 */
public class Arcade extends Game {

    public Random random = new Random();
    public List<Kit> kits;

    public BukkitTask task;
    public int shuffleTimer;

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
        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), this::randomizeKits, 6*20);
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

    public void startShuffleTask(){
        if (this.state == GameState.ENDED || this.state == GameState.FINISHED) return;
        this.shuffleTimer = 45;

        if (task != null) {
            task.cancel();
            task = null;
        }

        task = new BukkitRunnable(){
            @Override
            public void run() {
                if(shuffleTimer <= 0){
                    randomizeKits();
                    this.cancel();
                    return;
                }

                switch (shuffleTimer){
                    case 45:
                    case 30:
                    case 15:
                    case 3:
                    case 2:
                    case 1:{
                        playSound(Sound.NOTE_PIANO, 1L, 1L);
                        broadcast(Game.PREFIX + ChatColor.YELLOW + "Kits will be shuffled in " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(shuffleTimer) + ChatColor.YELLOW + ".");
                        break;
                    }
                    default:{
                        break;
                    }
                }
                shuffleTimer--;
            }
        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
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
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        int kitCount = kits.size();
        int rand = random.nextInt(kitCount);
        Kit chosenKit = kits.get(rand);

        playerData.setSelectedKit(chosenKit);
        chosenKit.getArmor().apply(player);
        player.getInventory().setContents(chosenKit.getItems().getItems());

        chosenKit.getAbilities().stream().map(Ability::getIcon).filter(Objects::nonNull).forEach(player.getInventory()::addItem);
        chosenKit.getAbilities().forEach(ability -> ability.onApply(player));
        chosenKit.getPotionEffects().forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        ItemStack item = playerData.getRefillType().getItem();
        if (item.getType() != Material.AIR) {
            while (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            }
        }

        player.updateInventory();
        player.closeInventory();
        player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "You have been given " + ChatColor.LIGHT_PURPLE + chosenKit.getName() + ChatColor.YELLOW + " kit.");
    }

    public void randomizeKits(){
        getAlivePlayers().forEach(player -> applyRandomKit(player.toPlayer()));
        startShuffleTask();
    }

    @Override
    public List<String> getSidebar(Player player) {
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> toReturn = new ArrayList<>();
        toReturn.add(" ");
        toReturn.add(CC.LIGHT_PURPLE + "Event: " + CC.YELLOW + getType().getShortName());
        toReturn.add(CC.LIGHT_PURPLE + "Players: " + CC.YELLOW + getAlivePlayers().size() + "/" + getPlayers().size());
        if(this.state == GameState.STARTED){
            if(data.getSelectedKit() != null) toReturn.add(CC.LIGHT_PURPLE + "Kit: " + ChatColor.YELLOW + data.getSelectedKit().getName());
            toReturn.add(CC.LIGHT_PURPLE + "Shuffling in: " + ChatColor.YELLOW + shuffleTimer + "s");
        }
        return toReturn;
    }

}

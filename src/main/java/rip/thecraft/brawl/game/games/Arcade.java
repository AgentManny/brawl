package rip.thecraft.brawl.game.games;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.game.*;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        this.kits = Brawl.getInstance().getKitHandler().getKits().stream()
                .filter(k -> !k.getAbilities().isEmpty())
                .collect(Collectors.toList());

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            player.teleport(getRandomLocation());
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
        super.end();
    }

    @Override
    public void clear() {
        getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            data.setPreviousKit(data.getSelectedKit());
            data.setSelectedKit(null);
        });
    }

    @Override
    public boolean eliminate(Player player, Location location, GameElimination elimination) {
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        data.setPreviousKit(data.getSelectedKit());
        data.setSelectedKit(null);
        return super.eliminate(player, location, elimination);
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

//                    int newRoundSoups = 10;
//                    for (GamePlayer alivePlayer : getAlivePlayers()) {
//                        Player player = alivePlayer.toPlayer();
//                        if (player != null) {
//                            player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.BOLD + newRoundSoups + ChatColor.GREEN + " Soups" + ChatColor.GRAY + " (New Round)");
//                            for (int i = 0; i < newRoundSoups; i++) {
//                                player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
//                            }
//                        }
//                    }

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

    public void applyRandomKit(Player player){
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        int kitCount = kits.size();
        int rand = random.nextInt(kitCount);

        Kit selectedKit = playerData.getSelectedKit();
        Kit chosenKit = kits.get(rand);

        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] items = new ItemStack[36];
        ItemStack[] kitContents = chosenKit.getItems().getItems();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP)) {
                items[i] = item;
            }
        }

        for (int i = 0; i < kitContents.length; i++) {
            ItemStack kitContent = kitContents[i];
            if (kitContent != null && kitContent.getType() != Material.AIR) {
                items[i] = kitContents[i];
            }
        }

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        chosenKit.getArmor().apply(player);
        player.getInventory().setContents(items);

        chosenKit.getAbilities().stream().map(Ability::getIcon).filter(Objects::nonNull).forEach(player.getInventory()::addItem);
        chosenKit.getAbilities().forEach(ability -> ability.onApply(player));
        chosenKit.getPotionEffects().forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        if (selectedKit == null) {
            ItemStack item = new ItemStack(Material.MUSHROOM_SOUP, 1);
            if (item.getType() != Material.AIR) {
                while (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                }
            }
        }

        playerData.setSelectedKit(chosenKit);

        player.updateInventory();
        player.closeInventory();
        player.sendMessage(Game.PREFIX + ChatColor.YELLOW + "You have been given " + ChatColor.LIGHT_PURPLE + chosenKit.getName() + ChatColor.YELLOW + " kit.");
    }

    public void randomizeKits(){
        Collections.shuffle(kits);
        getAlivePlayers().forEach(player -> applyRandomKit(player.toPlayer()));
        startShuffleTask();
    }

    @Override
    public List<String> getSidebar(Player player) {
        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.WHITE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.WHITE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        if(this.state == GameState.STARTED){
            if(data.getSelectedKit() != null) toReturn.add(CC.WHITE + "Kit: " + ChatColor.LIGHT_PURPLE + data.getSelectedKit().getName());
            toReturn.add(CC.WHITE + "Shuffling in: " + ChatColor.LIGHT_PURPLE + shuffleTimer + "s");
        }
        return toReturn;
    }

}

package gg.manny.brawl.game.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.game.Game;
import gg.manny.brawl.game.GameFlag;
import gg.manny.brawl.game.GameState;
import gg.manny.brawl.game.GameType;
import gg.manny.brawl.game.team.GamePlayer;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.util.DurationFormatter;
import gg.manny.pivot.util.chatcolor.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OITC extends Game implements Listener {

    public OITC() {
        super(GameType.OITC, GameFlag.NO_FALL);
    }

    private Map<GamePlayer, Integer> killsMap;

    @Override
    public void setup() {
        this.killsMap = new HashMap<>();
        Collections.shuffle(players);

        this.setDefaultLocation(this.getLocationByName("SpectatorLobby"));

        this.getAlivePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.toPlayer();
            this.applyInventory(player);
            player.teleport(this.getRandomLocation());
        });

        this.startTimer(5, true);
    }

    @Override
    public List<String> getSidebar(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(CC.DARK_PURPLE + "Event: " + CC.LIGHT_PURPLE + getType().getShortName());
        toReturn.add(CC.DARK_PURPLE + "Players: " + CC.LIGHT_PURPLE + getAlivePlayers().size() + "/" + getPlayers().size());
        toReturn.add(CC.BLUE + CC.SCOREBAORD_SEPARATOR);
        if (this.state == GameState.STARTED) {
            LinkedHashMap<GamePlayer, Integer> sortedKills = sortByValues(killsMap);
            int index = 0;

            for (Map.Entry<GamePlayer, Integer> entry : sortedKills.entrySet()) {
                index++;
                if (index > 5) break;

                toReturn.add(CC.DARK_PURPLE + index + ". " + CC.WHITE + entry.getKey().getName() + " " + CC.LIGHT_PURPLE + entry.getValue());
            }
            toReturn.add(CC.GREEN + CC.SCOREBAORD_SEPARATOR);

            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (playerData.hasCooldown("Grace Period")) {
                toReturn.add(CC.DARK_PURPLE + "Grace Period: " + CC.LIGHT_PURPLE +  DurationFormatter.getRemaining(playerData.getCooldown("Grace Period").getRemaining()));
            }
            if (playerData.hasCooldown("Double Jump")) {
                toReturn.add(CC.DARK_PURPLE + "Double Jump: " + CC.LIGHT_PURPLE +  DurationFormatter.getRemaining(playerData.getCooldown("Double Jump").getRemaining()));
            }
        } else if (this.state == GameState.FINISHED) {
            boolean winners = this.winners.size() > 1;
            if (winners) {
                toReturn.add(CC.DARK_PURPLE + "Winners: ");
                for (GamePlayer winner : this.winners) {
                    toReturn.add(CC.LIGHT_PURPLE + "  " + winner.getName());
                }
            } else if (!this.winners.isEmpty()) {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.LIGHT_PURPLE + this.winners.get(0).getName());
            } else {
                toReturn.add(CC.DARK_PURPLE + "Winner: " + CC.RED + "None");
            }
        } else {
            toReturn.add(CC.LIGHT_PURPLE + "Waiting...");
        }
        return toReturn;
    }



    private void applyInventory(Player player) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        item = new ItemStack(Material.WOOD_PICKAXE);
        meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        player.updateInventory();
    }

    public void addKill(GamePlayer player) {
        int kills = getKills(player);
        this.killsMap.put(player, kills);

        if (kills == 20) {
            // Find a winner
            this.winners.add(player);
            this.end();
        } else if (kills == 19) {
            broadcast(Game.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " needs " + ChatColor.LIGHT_PURPLE + "1" + ChatColor.YELLOW + " more kills to win!");
        }
    }

    public void removeKill(GamePlayer player) {
        killsMap.put(player, Math.max(getKills(player) - 1, 0));
    }

    public int getKills(GamePlayer player) {
        return this.killsMap.getOrDefault(player, 0);
    }

    private LinkedHashMap<GamePlayer, Integer> sortByValues(Map<GamePlayer, Integer> map) {
        LinkedList<Map.Entry<GamePlayer, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<GamePlayer, Integer> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<GamePlayer, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


}

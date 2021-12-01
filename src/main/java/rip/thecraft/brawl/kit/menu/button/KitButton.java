package rip.thecraft.brawl.kit.menu.button;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.statistic.KitStatistic;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class KitButton extends Button {

    private final Kit kit;

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = ItemBuilder.wrap(kit.getDescription(), CC.GRAY, 32, false);

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        KitStatistic statistic = playerData.getStatistic().get(kit);

        Map<String, Long> kitRentals = playerData.getKitRentals();
        boolean rental = kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis();
        boolean canTrial = statistic != null && statistic.getTrialPass() >= 1;
        boolean canKitPass = playerData.getKitPasses() >= 1;
        boolean unlocked = playerData.hasKit(kit);

        lore.add(0, " ");
        lore.add(0, ChatColor.DARK_GRAY + (kit.isFree() ? "Free" : unlocked ? "Unlocked" : "Locked"));

        if (!kit.getDescription().isEmpty()) {
            lore.add("");
        }
        if (statistic != null) {
            lore.add(CC.GRAY + "Kills: " + CC.WHITE + statistic.getKills());
            lore.add(CC.GRAY + "Deaths: " + CC.WHITE + statistic.getDeaths());
            lore.add(CC.GRAY + "Uses: " + CC.WHITE + statistic.getUses());
            statistic.getProperties().forEach((name, value) -> lore.add(CC.GRAY + WordUtils.capitalizeFully(name.toLowerCase().replace("_", " ")) + ": " + CC.WHITE + value));
        }
        lore.add("");

        String value;
        if (rental) {
            value = CC.YELLOW + "Time remaining: " + CC.WHITE + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(kitRentals.get(kit.getName()) - System.currentTimeMillis()));
        } else if (unlocked) {
            value = CC.GREEN + "Click to use this kit";
        } else if (canTrial) {
            value = CC.LIGHT_PURPLE + "Click to trial this kit";
        } else if (kit.getRankType() != RankType.NONE) {
            value = CC.RED + "Exclusive to " + kit.getRankType().getDisplayName() + CC.RED + " rank";
        } else {
            value = CC.YELLOW + "Click to unlock this kit";
        }
//        if (canKitPass) {
//            lore.add(ChatColor.LIGHT_PURPLE + "\u00bb " + ChatColor.GRAY + "Kit passes: " + ChatColor.LIGHT_PURPLE + playerData.getKitPasses());
//        }
        if (!playerData.hasKit(kit) && canKitPass) {
            lore.add(value);
            lore.add(ChatColor.GRAY + "\u00bb " + CC.YELLOW + "Right Click to use a kit pass " + ChatColor.GRAY + "(" + playerData.getKitPasses() + "x)");
        } else {
            lore.add(CC.GRAY + "\u00bb " + value);
        }
        return new ItemBuilder(kit.getIcon())
                .name((unlocked ? rental ? CC.YELLOW : CC.GREEN : canTrial ? CC.LIGHT_PURPLE : CC.RED) + CC.BOLD + kit.getName())
                .amount(1).lore(lore)
                .create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        // TODO ADD A FREE KIT IMPLEMENTATION

        if(!kit.isEnabled()){
            player.sendMessage(ChatColor.RED + "This kit is currently disabled.");
            return;
        }

        if (playerData.hasKit(kit)) {
            kit.apply(player, true, true);
        } else if (clickType == ClickType.RIGHT) {
            if (playerData.getKitPasses() == 0) {
                player.sendMessage(ChatColor.RED + "You don't have any kit passes.");
                return;
            }

            playerData.setKitPasses(playerData.getKitPasses() - 1);
            playerData.addRentalKit(kit, 30, TimeUnit.MINUTES);
            clicked(player, slot, clickType);
        } else {
            KitStatistic statistic = playerData.getStatistic().get(kit);
            if (!playerData.hasKit(kit) && statistic != null && statistic.getTrialPass() >= 1) {
                statistic.setTrialPass(statistic.getTrialPass() - 1);
                playerData.addRentalKit(kit, 15, TimeUnit.MINUTES);
                clicked(player, slot, clickType);
            } else {
                player.sendMessage(CC.RED + "You don't have permission to use this kit.");
            }
        }
    }
}

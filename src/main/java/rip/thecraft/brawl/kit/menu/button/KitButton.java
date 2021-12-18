package rip.thecraft.brawl.kit.menu.button;

import gg.manny.hologram.HologramPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

        Map<String, Long> kitRentals = playerData.getKitRentals();
        boolean rental = kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis();
        boolean unlocked = playerData.hasKit(kit) || kit.isFreeAccess();
        boolean disabled = !kit.isEnabled();

        if (kit.isFreeAccess()) {
            lore.add(0, " ");
            lore.add(0, ChatColor.GREEN.toString() + ChatColor.BOLD + "- FREE -");
        }

        lore.add("");
        if (rental) {
            lore.add(ChatColor.YELLOW + "Expires in: " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(kitRentals.get(kit.getName()) - System.currentTimeMillis())));
            lore.add(" ");
        }
        String value;
        if (disabled) {
            value = CC.DARK_RED + "\u2716" + ChatColor.RED + " Kit currently disabled :(";
        } else if (unlocked) {
            value = ChatColor.YELLOW + "Click to play this kit.";
        } else {
            value = CC.DARK_RED + "\u2716" + ChatColor.RED + " You don't own this kit.";
        }
        lore.add(value);
        ItemBuilder item = new ItemBuilder(kit.getIcon())
                .name((unlocked ? CC.YELLOW : CC.RED) + CC.BOLD + kit.getName())
                .amount(1).lore(lore);
        if (disabled) {
            boolean legacyVersion = HologramPlugin.getInstance().onLegacyVersion(player);
            item.material(legacyVersion ? Material.STAINED_GLASS_PANE : Material.BARRIER);
            item.data(legacyVersion ? (byte) 14 : 0);
        }
        ItemStack itemStack = item.create();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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

            if (kit.getRankType() == RankType.CHAMPION) {
                player.sendMessage(ChatColor.RED + "You can't use kit passes on this kit.");
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

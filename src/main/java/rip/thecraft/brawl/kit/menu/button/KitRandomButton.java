package rip.thecraft.brawl.kit.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import gg.manny.streamline.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitRandomButton extends Button {

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
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Unsure what to play?");
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to choose a random kit");
        lore.add(ChatColor.GRAY + "that you have access to.");
        lore.add("");
        lore.add(CC.YELLOW + "Click to use a random kit");
        return new ItemBuilder(Material.NETHER_STAR)
                .name(CC.YELLOW + ChatColor.BOLD + "Random Kit").lore(lore).amount(1).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<Kit> kits = Brawl.getInstance().getKitHandler().getKits().stream().filter(playerData::hasKit).collect(Collectors.toList());
        Kit kit = kits.get(Brawl.RANDOM.nextInt(kits.size()));
        kit.apply(player, true, true);
    }
}

package rip.thecraft.brawl.kit.editor.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitInventoryMenu extends Menu {
    {
        setUpdateAfterClick(true);
    }

    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return "Editing kit: " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();


        return buttons;
    }

    @Override
    public void onOpen(Player player) {
        player.sendMessage(" ");
        player.sendMessage(ChatColor.DARK_PURPLE + "You are now editing kit: " + ChatColor.WHITE + kit.getName() + ChatColor.DARK_PURPLE + ".");
        player.sendMessage(ChatColor.GRAY + "Once you close this inventory, it'll automatically save to disk.");
        player.sendMessage(ChatColor.GRAY + "* Ability items are automatically added.");
        player.sendMessage(" ");
        player.sendMessage(ChatColor.RED + "Note: " + ChatColor.GRAY + "Don't add refill items (e.g. soup/potions) as they are managed per player.");
        player.sendMessage(" ");

        kit.getArmor().apply(player);
        player.getInventory().setContents(kit.getItems().getItems());
        kit.getPotionEffects().forEach(potionEffect -> player.addPotionEffect(potionEffect, true));

        player.updateInventory();
    }

    @Override
    public void onClose(Player player) {
        player.sendMessage("I should prolly save the kit.");
    }




}

package rip.thecraft.brawl.killstreak.type;

import rip.thecraft.brawl.killstreak.Killstreak;
import rip.thecraft.brawl.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FullRepair extends Killstreak {

    @Override
    public int[] getKills() {
        return new int[] { 10 };
    }

    @Override
    public String getName() {
        return "Full Repair";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GRAY;
    }

    @Override
    public Material getType() {
        return Material.ANVIL;
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public void onActivate(Player player, PlayerData playerData) {
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().getMaxDurability() > 0) {
                content.setDurability((short) 0);
            }
        }

        for (ItemStack content : player.getInventory().getArmorContents()) {
            if (content != null && content.getType().getMaxDurability() > 0) {
                content.setDurability((short) 0);
            }
        }

        player.sendMessage(ChatColor.GREEN + "You have repaired all items in your inventory.");
        player.updateInventory();
    }
}

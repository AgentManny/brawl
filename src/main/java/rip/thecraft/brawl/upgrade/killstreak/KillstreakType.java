package rip.thecraft.brawl.upgrade.killstreak;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@AllArgsConstructor
public enum KillstreakType {

    FULL_REPAIR(
            "Full Repair",
            ChatColor.GRAY, Material.ANVIL,
            10,
            (player) -> {
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
            },
            null
    ),

    GOLDEN_APPLES(
      "Golden Apples", ChatColor.GOLD, Material.GOLDEN_APPLE,
      5, null, null
    );



    private String name;
    private ChatColor color;
    private Material icon;

    private int requiredKills;

    private Consumer<Player> kill;
    private Consumer<Player> activate;

    public void onKill(Player player) {
        if (kill != null) {
            kill.accept(player);
        }
    }

    public void onActivate(Player player) {
        if (activate != null) {
            activate.accept(player);
        }
    }

}

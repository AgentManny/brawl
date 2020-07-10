package rip.thecraft.brawl.kit.command;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.util.conversation.PromptBuilder;
import rip.thecraft.spartan.command.Command;

public class KitBuilderCommand {

    @Command(names = "kit build", permission = "op")
    public static void buildKit(Player player, String name) {
        player.sendMessage(ChatColor.GREEN + "Creating " + name + " kit...");

        Kit kit = new Kit(name);

        new PromptBuilder(player, ChatColor.GREEN + "Hold an item in your inventory to choose as an icon. (Type 'confirm' to set)")
                .input((input) -> {
                    ItemStack item = player.getItemInHand();
                    if (item != null && item.getType() != Material.AIR && input.equalsIgnoreCase("confirm")) {
                        player.sendMessage(ChatColor.GREEN + "Confirmed! Set icon to " + ChatColor.WHITE + WordUtils.capitalizeFully(item.getType().name()) + ChatColor.GREEN + ".");
                        kit.setIcon(item);
                        setDescription(kit, player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Cancelled! Kit creation failed.");
                    }
                }).createConversation();
    }

    private static void setDescription(Kit kit, Player player) {
        new PromptBuilder(player, ChatColor.GREEN + "Enter the kit description:")
                .input((input) -> {
                    player.sendMessage(ChatColor.GREEN + "Description set to: " + ChatColor.YELLOW + input);
                    kit.setDescription(input);
                    setPrice(kit, player);
                }).start();
    }

    private static void setPrice(Kit kit, Player player) {
        new PromptBuilder(player, ChatColor.GREEN + "Enter a price for your kit (Typing 0 will assume it's free):")
                .input((input) -> {
                    int count;
                    try {
                        count = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Cancelled! Kit creation failed.");
                        return;
                    }
                    player.sendMessage(ChatColor.GREEN + "Price set to: " + ChatColor.YELLOW + count + " credits");
                    kit.setPrice(count);
                    setRankType(kit, player);
                }).start();
    }

    private static void setRankType(Kit kit, Player player) {
        new PromptBuilder(player, ChatColor.GREEN + "Enter what rank should be allowed to use this kit (Typing NONE will assume it's for any ranks):")
                .input((input) -> {
                    RankType rankType;
                    try {
                        rankType = RankType.valueOf(input.toUpperCase().replace("_", " "));
                    } catch (Exception exception) {
                        player.sendMessage(ChatColor.RED + "Cancelled! Kit creation failed.");
                        return;
                    }
                    player.sendMessage(ChatColor.GREEN + "Kit is now restricted to: " + ChatColor.YELLOW + input);
                    kit.setRankType(rankType);
                }).start();
    }
}

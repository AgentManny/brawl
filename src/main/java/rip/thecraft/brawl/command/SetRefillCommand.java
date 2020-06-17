package rip.thecraft.brawl.command;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class SetRefillCommand {

    @Command(names = "setrefill")
    public void execute(Player player, @Param(defaultValue = "_", name = "soup|potion") String source) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        RefillType refillType;
        if (source.equalsIgnoreCase("_")) {
            refillType = playerData.getRefillType() == RefillType.SOUP ? RefillType.POTION : RefillType.SOUP;
        } else {
            try {
                refillType = RefillType.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                refillType = null;
            }
        }
        setRefillType(player, playerData, refillType);
    }

    private void setRefillType(CommandSender sender, PlayerData playerData, RefillType refillType) {
        if (refillType == null || refillType == RefillType.NONE) {
            sender.sendMessage(ChatColor.RED + "You must provide a valid healing method.");
            sender.sendMessage(ChatColor.RED      + "Available healing methods: " + ChatColor.GRAY + "[Soup, Potion]");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Set your primarily healing method to " + ChatColor.WHITE + WordUtils.capitalizeFully(refillType.name().toLowerCase()) + ChatColor.GREEN + ".");
        playerData.setRefillType(refillType);
    }

}

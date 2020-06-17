package rip.thecraft.brawl.warp.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.warp.Warp;
import rip.thecraft.brawl.warp.WarpManager;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

@RequiredArgsConstructor
public class WarpCommand {

    private final WarpManager wm;

    @Command(names = { "warp", "warps", "go", "goto" })
    public void execute(Player sender, @Param(defaultValue= "list", name = "warp") Warp warp) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(sender);
        if (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.FIGHTING) {
            playerData.warp(warp.getName(), warp.getLocation(), 10, () -> {
                playerData.setSpawnProtection(false);
                playerData.setDuelArena(false);
                Kit kit = Brawl.getInstance().getKitHandler().getKit(warp.getKit());
                if (kit != null) {
                    kit.apply(sender, true, true);
                }

                sender.teleport(warp.getLocation());
                sender.setFallDistance(0);
            });
        } else {
            sender.sendMessage(ChatColor.RED + "You must have spawn protection to warp");
        }
    }

    @Command(names = { "warps help", "warp help"}, permission = "op")
    public void help(Player sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + "Warp Commands");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp <warpName>" + ChatColor.GRAY + " (Teleport to a warp)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp create <warpName>" + ChatColor.GRAY + " (Create a warp)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp remove <warpName>" + ChatColor.GRAY + " (Remove a warp)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp setlocation <warpName>" + ChatColor.GRAY + " (Update location of a warp)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp kit [kit]" + ChatColor.GRAY + " (Restrict a warp to a kit)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /warp save" + ChatColor.GRAY + " (Save warps)");
        sender.sendMessage(" ");
    }

    @Command(names = {"warp create", "warps create"}, permission = "op")
    public void create(Player sender, String name) {
        if(wm.getWarp(name) != null) {
            sender.sendMessage(ChatColor.RED + "Warp '" + name + "' is already created!");
            return;
        }

        wm.createWarp(name, sender.getLocation(), null);
        sender.sendMessage(ChatColor.YELLOW + "Created warp " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + ".");
    }

    @Command(names = {"warp remove", "warps remove"}, permission = "op")
    public void remove(Player sender, @Param(name = "warpName") Warp warp) {
        sender.sendMessage(ChatColor.YELLOW + "Removed the warp " + ChatColor.LIGHT_PURPLE + warp.getName() + ChatColor.YELLOW + ".");
        wm.removeWarp(warp.getName());
    }

    @Command(names = {"warp setlocation", "warps setlocation"}, permission = "op")
    public void setLocation(Player sender, @Param(name = "warpName") Warp warp) {
        warp.setLocation(sender.getLocation());
        sender.sendMessage(ChatColor.YELLOW + "You have updated the location for the warp " + ChatColor.GOLD + warp.getName() + ChatColor.YELLOW + ".");
    }


    @Command(names = {"warp kit", "warps kit"}, permission = "op")
    public void setKit(Player sender, @Param(name = "warpName") Warp warp, String kitName) {

        Kit kit = Brawl.getInstance().getKitHandler().getKit(kitName);
        warp.setKit(kit.getName());
        sender.sendMessage(ChatColor.YELLOW + "You have set the warp kit restriction to " + ChatColor.LIGHT_PURPLE + (kit == null ? "None" : kit.getName()) + ChatColor.YELLOW + ".");
    }


    @Command(names = {"warp save", "warps save"}, permission = "op")
    public void save(Player sender) {
        wm.save();
        sender.sendMessage(ChatColor.YELLOW + "Saved warp(s).");
    }




}

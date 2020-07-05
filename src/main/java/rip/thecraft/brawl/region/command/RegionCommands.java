package rip.thecraft.brawl.region.command;

import lombok.RequiredArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.region.Region;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.region.selection.Selection;
import rip.thecraft.spartan.command.Command;

@RequiredArgsConstructor
public class RegionCommands {

    private final Brawl plugin;

    @Command(names = "region remove", permission = "op")
    public void remove(CommandSender sender, String name) {
        Region region;
        if((region = plugin.getRegionHandler().get(name)) == null) {
            sender.sendMessage(ChatColor.RED + "Region " + name + " not found.");
            return;
        }

        plugin.getRegionHandler().remove(region);
        sender.sendMessage(ChatColor.RED + "Removed " + region.getName() + " region.");

    }

    @Command(names = "region setcolor", permission = "op")
    public void setColor(CommandSender sender, String name) {
        Region region;
        if((region = plugin.getRegionHandler().get(name)) == null) {
            sender.sendMessage(ChatColor.RED + "Region " + name + " not found.");
            return;
        }

        region.setPrefix(ChatColor.translateAlternateColorCodes('&', name));
        sender.sendMessage(ChatColor.GREEN + "Set region " + ChatColor.WHITE + region.getName() + " region to " + region.getPrefix() + region.getName() + ChatColor.GREEN + ".");
    }


    @Command(names = "region list", permission = "op")
    public void list(CommandSender sender) {
        plugin.getRegionHandler().getRegions().forEach(region -> {
            new FancyMessage(ChatColor.GOLD + region.getName() + ChatColor.GRAY + "[" + region.getType().name() + "]" + " (" + region.getX1() + ", " + region.getY1() + ", " + region.getZ1() + ") (" + region.getX2() + ", " + region.getY2() + ", " + region.getZ2() + ")")
                    .tooltip(ChatColor.YELLOW + "Click to teleport to this region")
                    .command("/tppos " + region.getCenter().getX() + " " + region.getCenter().getY() + " " + region.getCenter().getZ() + " " + region.getWorldName())
                    .send(sender);
        });
    }

    @Command(names = "region wand", permission = "op")
    public void execute(Player player) {
        if (player.getInventory().contains(Selection.SELECTION_WAND)) {
            player.getInventory().remove(Selection.SELECTION_WAND);
        }

        player.getInventory().addItem(Selection.SELECTION_WAND);
    }

    @Command(names = "region create", permission = "op")
    public void execute(Player player, String name, String type) {
        Selection selection = Selection.createOrGetSelection(player);

        if (!selection.isFullObject()) {
            player.sendMessage(ChatColor.RED + "You don't have a selection selected");
            return;
        }

        if (plugin.getRegionHandler().get(name) != null) {
            player.sendMessage(ChatColor.RED + "Region " + name + " already exists.");
            return;
        }

        RegionType regionType;
        try {
            regionType = RegionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(ChatColor.RED + "Region type " + type.toUpperCase() + " not found.");
            return;
        }
        Region region = new Region(selection.getPoint1(), selection.getPoint2());
        region.setType(regionType);
        region.setName(name);

        plugin.getRegionHandler().add(region);
        player.sendMessage(ChatColor.GREEN + "Created region " + name + ".");
    }

}

package gg.manny.brawl.region.command;

import com.google.common.base.Strings;
import com.sk89q.worldedit.bukkit.selections.Selection;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.Locale;
import gg.manny.brawl.region.Region;
import gg.manny.brawl.region.RegionType;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RegionCommand {

    private final Brawl plugin;

    @Command(names = { "region", "region help" }, permission = "op")
    public void execute(CommandSender sender) {
        Locale.COMMAND_REGION_HELP.toList().forEach(line -> sender.sendMessage(line.replace("{LINE}", Strings.repeat("-", 51))));
    }

    @Command(names = "region remove", permission = "op")
    public void executeRemove(CommandSender sender, String name) {
        Region region;
        if((region = plugin.getRegionHandler().get(name)) == null) {
            sender.sendMessage(Locale.COMMAND_REGION_ERROR_NOT_FOUND.format(name));
            return;
        }

        plugin.getRegionHandler().remove(region);
        sender.sendMessage(Locale.COMMAND_REGION_REMOVE.format(name));

    }

    @Command(names = "region list", permission = "op")
    public void executeList(CommandSender sender) {
        for(String line : Locale.COMMAND_REGION_LIST.toList()) {
            if(line.contains("{NAME}")) {
                for(Region region : plugin.getRegionHandler().getRegions()) {
                    String newLine = line
                            .replace("{NAME}", region.getName())
                            .replace("{TYPE}", region.getType().name())
                            .replace("{TYPE:FORMAT}", WordUtils.capitalizeFully(region.getType().name().toLowerCase()))
                            .replace("{X}", region.getX1() + "")
                            .replace("{Y}", region.getY1() + "")
                            .replace("{Z}", region.getZ1() + "")
                            .replace("{X2}", region.getX2() + "")
                            .replace("{Y2}", region.getY2() + "")
                            .replace("{Z2}", region.getZ2() + "");
                    sender.sendMessage(newLine);
                }
                continue;
            }
            sender.sendMessage(line.replace("{LINE}", Strings.repeat("-", 51)));
        }
    }

    @Command(names = "region create", permission = "op")
    public void execute(Player player, String name, String type) {
        Selection selection = plugin.getWorldEdit().getSelection(player);
        if (selection == null) {
            player.sendMessage(Locale.COMMAND_REGION_ERROR_NOT_SELECTED.format());
            return;
        }

        if (plugin.getRegionHandler().get(name) != null) {
            player.sendMessage(Locale.COMMAND_REGION_ERROR_ALREADY_EXISTS.format(name));
            return;
        }

        RegionType regionType = null;
        try {
            regionType = RegionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(Locale.COMMAND_REGION_ERROR_TYPE_NOT_FOUND.format(type));
        } finally {
            Region region = new Region(selection.getMaximumPoint(), selection.getMinimumPoint());
            region.setType(regionType);
            region.setName(name);

            plugin.getRegionHandler().add(region);
            region.save();
            player.sendMessage(Locale.COMMAND_REGION_CREATE.format(name));
        }
    }

}

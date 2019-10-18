package gg.manny.brawl.kit.command.adapter;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.quantum.command.adapter.CommandTypeAdapter;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class KitTypeAdapter implements CommandTypeAdapter<Kit> {

    private final Brawl brawl;

    @Override
    public Kit transform(CommandSender sender, String source) {
        Kit kit = brawl.getKitHandler().getKit(source);
        if (kit == null) {
            sender.sendMessage(CC.RED + "Kit " + source + " not found.");
        }
        return kit;
    }

    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        for (Kit kit : brawl.getKitHandler().getKits()) {
            if (StringUtils.startsWithIgnoreCase(kit.getName(), source)) {
                completions.add(kit.getName());
            }
        }
        return completions;
    }

}

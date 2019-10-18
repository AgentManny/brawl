package gg.manny.brawl.kit.menu.button;

import com.google.common.base.Strings;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class KitRandomButton extends Button {

    private final Brawl plugin;
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = ItemBuilder.wrap("Not sure what to pick? Click to choose a random kit that you have access to.", CC.GRAY, 30);
        lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        lore.add("");
        lore.add(CC.GRAY + "\u00bb " + CC.LIGHT_PURPLE + "Click to use a random kit" + CC.GRAY + " \u00ab");
        lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
        return new ItemBuilder(kit.getIcon())
                .name(CC.LIGHT_PURPLE + "Select a random kit").lore(lore).create();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        List<Kit> kits = plugin.getKitHandler().getKits().stream().filter(playerData::hasKit).collect(Collectors.toList());
        Kit kit = kits.get(Brawl.RANDOM.nextInt(kits.size()));
        kit.apply(player, true, true);
    }
}

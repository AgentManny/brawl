package rip.thecraft.brawl.kit.menu.button;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class KitRandomButton extends Button {

    private final Kit kit;

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

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
    public void clicked(Player player, int slot, ClickType clickType) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        List<Kit> kits = Brawl.getInstance().getKitHandler().getKits().stream().filter(playerData::hasKit).collect(Collectors.toList());
        Kit kit = kits.get(Brawl.RANDOM.nextInt(kits.size()));
        kit.apply(player, true, true);
    }
}

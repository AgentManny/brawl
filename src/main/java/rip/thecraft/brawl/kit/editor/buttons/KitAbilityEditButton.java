package rip.thecraft.brawl.kit.editor.buttons;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class KitAbilityEditButton extends Button {

    private Kit kit;
    private Ability ability;

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ability.getName();
    }

    @Override
    public Material getMaterial(Player player) {
        return ability.getType();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList(
                " ",
                CC.translate("Click here to remove this ability from kit &f" + kit.getName() + "&7."));
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (kit.getAbilities().remove(ability)) {
            player.closeInventory();
            player.sendMessage(CC.GREEN + "You have successfully removed " + ability.getName() + " from kit " + kit.getName() + ".");
        } else {
            player.closeInventory();
            player.sendMessage(CC.RED + "An issue has occurred while removing " + ability.getName() + " from kit " + kit.getName() + ".");
            player.sendMessage(CC.RED + "It is likely that the kit did not have the " + ability.getName() + " ability.");
        }
    }
}

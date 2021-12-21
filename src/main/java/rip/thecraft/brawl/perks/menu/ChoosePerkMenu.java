package rip.thecraft.brawl.perks.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.perks.menu.button.PerkRemoveButton;
import rip.thecraft.brawl.perks.menu.button.PerkSwitchButton;
import rip.thecraft.brawl.perks.Perk;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.menu.buttons.BackButton;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ChoosePerkMenu extends Menu {

    private int slot;

    @Override
    public String getTitle(Player player) {
        return "Choose a perk";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        int x = 1;
        int y = 1;
        for (Perk perk : Perk.values()) {
            if(perk.getPerkSlot().getValue() == slot){
                buttons.put(getSlot(x, y), new PerkSwitchButton(playerData, perk, slot, perk.contains(playerData.getActivePerks())));
                if (x++ >= 7) {
                    x = 1;

                    y++;
                }
            }
        }


        buttons.put(39, new BackButton(new PerkMenu()));
        buttons.put(41, new PerkRemoveButton(playerData, slot));
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 45;
    }
}

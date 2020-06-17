package rip.thecraft.brawl.market;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.market.items.InventoryFillButton;
import rip.thecraft.brawl.market.items.KitButton;
import rip.thecraft.brawl.market.items.MarketItem;
import rip.thecraft.brawl.market.items.StatsResetButton;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketMenu extends Menu {
    {
        setAutoUpdate(true);
        setUpdateAfterClick(true);
    }

    private List<MarketItem> items = new ArrayList<>();

    public MarketMenu() {
        items.add(new InventoryFillButton());
        items.add(new StatsResetButton());

        for (Kit kit : Brawl.getInstance().getMarketHandler().getSellingKits()) {
            KitButton button = new KitButton(kit, Brawl.getInstance().getMarketHandler().getMultiplier());
            items.add(button);
        }

    }

    @Override
    public String getTitle(Player player) {
        return "Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (MarketItem item : items) {
            buttons.put(item.getWeight(), item);
        }
        return buttons;
    }
}

package gg.manny.brawl.market;

import gg.manny.brawl.market.items.InventoryFillButton;
import gg.manny.brawl.market.items.MarketItem;
import gg.manny.brawl.market.items.StatsResetButton;
import gg.manny.pivot.menu.Button;
import gg.manny.pivot.menu.Menu;
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

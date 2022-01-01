package rip.thecraft.brawl.spawn.market;

import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.market.items.*;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MarketMenu extends Menu {
    {
        setAutoUpdate(true);
        setUpdateAfterClick(true);
    }

    private static final long ROTATION_TIME = TimeUnit.HOURS.toMillis(24);

    private List<MarketItem> items = new ArrayList<>();

    private long lastRotation = -1L;

    public MarketMenu() {
        items.add(new GoldenAppleButton());
        items.add(new InventoryFillButton());
        items.add(new RepairButton());
        items.add(new KitPassButton());
        items.add(new StatsResetButton());
//        for (Kit kit : Brawl.getInstance().getMarketHandler().getSellingKits()) {
//            KitButton button = new KitButton(kit, Brawl.getInstance().getMarketHandler().getMultiplier());
//            items.add(button);
//        }
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

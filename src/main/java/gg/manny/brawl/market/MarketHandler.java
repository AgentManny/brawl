package gg.manny.brawl.market;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.util.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarketHandler {

    private List<Kit> sellingKits = new ArrayList<>();

    public MarketHandler() {
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                randomizeKits();
//                Bukkit.broadcastMessage(ChatColor.GREEN + "The item shop has been refreshed.");
//            }
//        }.runTaskTimer(Brawl.getInstance(), 200L, 6000L);
    }



    public void randomizeKits() {
        sellingKits.clear();
        List<Kit> kits = Brawl.getInstance().getKitHandler().getKits()
                .stream()
                .filter(kit -> !kit.isFree())
                .collect(Collectors.toList());
        if (kits.isEmpty()) return;

        Collections.shuffle(kits);

        for (int i = 0; i < 3; i++) {
            // I, 2, 3

            Kit kit = kits.get(MathUtil.getRandomInt(0, kits.size() - 1));
            if (kit != null && !sellingKits.contains(kit)) {
                sellingKits.add(kit);
            }

        }

    }
}

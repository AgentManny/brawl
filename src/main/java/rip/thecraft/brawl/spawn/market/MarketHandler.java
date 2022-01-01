package rip.thecraft.brawl.spawn.market;

import lombok.Getter;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarketHandler {

    //todo rework our shop system

    @Getter private List<Kit> sellingKits = new ArrayList<>();
    @Getter private double multiplier = 1.23;
    public MarketHandler() {
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                multiplier = (MathUtil.getRandomInt(25, 100) / 100D) + 1;
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

            Kit kit = kits.get(Brawl.RANDOM.nextInt(kits.size() - 1));
            if (kit != null && !sellingKits.contains(kit)) {
                sellingKits.add(kit);
            }

        }

    }
}

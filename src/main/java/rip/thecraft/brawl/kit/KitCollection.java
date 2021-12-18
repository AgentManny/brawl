package rip.thecraft.brawl.kit;

import net.minecraft.server.v1_8_R3.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitCollection {

    private String name;
    private String description;
    private int weight;

    private ItemStack icon;

    private Map<Integer, Kit> kits = new HashMap<>();

}

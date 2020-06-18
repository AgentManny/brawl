package rip.thecraft.brawl.upgrade.perk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum Perk {

    BULLDOZER(
            "Bulldozer",
            Material.BLAZE_POWDER,
            ChatColor.RESET + "Killing a player grants you " + ChatColor.RED + "Strength I" + ChatColor.RESET + " for 5 seconds.",
            10000
    );

    private String name;
    private Material icon;
    private String description;
    private int credits;

}

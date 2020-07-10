package rip.thecraft.brawl.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum ArmorUtil {
	
	DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
	IRON(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
	CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
	GOLD(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS),
	LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

	private final Material helmet, chestplate, leggings, boots;

	public static boolean contains(ArmorUtil armor, Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
       
        if (helmet == null || helmet.getType() != armor.helmet) {
        	return false;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != armor.chestplate) {
        	return false;
        }

        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != armor.leggings) return false;

        ItemStack boots = player.getInventory().getBoots();
        if(boots == null || boots.getType() != armor.boots) {
        	return false;
        }
        
		return true;
	}
	
}

package rip.thecraft.brawl.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public enum ArmorUtil {
	
	DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
	IRON(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
	CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
	GOLD(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS),
	LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
	
	@Getter
	private final Material helmet, chestplate, leggings, boots;
	
	//requiredargsconstructor wouldve saved so much time :( lombok <3
	ArmorUtil(Material helmet, Material chestplate, Material leggings, Material boots) {
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
	}
	
	
	public static boolean contains(ArmorUtil armor, Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
       
        if (helmet == null || helmet.getType() != armor.getHelmet()) {
        	return false;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != armor.getChestplate()) {
        	return false;
        }

        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != armor.getLeggings()) return false;

        ItemStack boots = player.getInventory().getBoots();
        if(boots == null || boots.getType() != armor.getBoots()) {
        	return false;
        }
        
		return true;
	}
	
}

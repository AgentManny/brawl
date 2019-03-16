package gg.manny.brawl.game;

import gg.manny.brawl.kit.type.RankType;
import gg.manny.brawl.kit.type.RarityType;
import gg.manny.pivot.util.inventory.ItemBuilder;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum GameType {

	SPLEEF(
			new ItemBuilder(Material.DIAMOND_SPADE).build(),
			RarityType.COMMON,
			RankType.BASIC,
			"Fight opponents. Last man standing wins.",
			40
	);

	private final String name;
	private final String shortName;

	private final String description;

	private final int maxPlayers;

	private boolean randomLocations = false;

	private final ItemStack icon;

	private final List<String> requiredLocations;

	private final RarityType rarityType;
	private final RankType rankType;

	GameType(ItemStack icon, RarityType rarityType, RankType rankType, String description, int maxPlayers) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.icon = icon;
		this.description = description;
		this.rarityType = rarityType;
		this.maxPlayers = maxPlayers;
		this.randomLocations = true;
		this.rankType = rankType;
		this.requiredLocations = new ArrayList<>(); //Random
	}

	GameType(ItemStack icon, RarityType rarityType, RankType rankType, String description, int maxPlayers, List<String> requiredLocations) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.description = description;
		this.icon = icon;
		this.rarityType = rarityType;
		this.rankType = rankType;
		this.maxPlayers = maxPlayers;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, ItemStack icon, RarityType rarityType, RankType rankType, String description, int maxPlayers, List<String> requiredLocations) {
		this.name = name;
		this.shortName = name;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.icon = icon;
		this.rarityType = rarityType;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, String shortName, ItemStack icon, RarityType rarityType, RankType rankType, String description, int maxPlayers, List<String> requiredLocations) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.shortName = shortName;
		this.description = description;
		this.icon = icon;
		this.rarityType = rarityType;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}
	
}
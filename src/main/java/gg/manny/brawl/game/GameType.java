package gg.manny.brawl.game;

import gg.manny.brawl.kit.type.RankType;
import gg.manny.pivot.util.ItemBuilder;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum GameType {

	SPLEEF(
			new ItemBuilder(Material.DIAMOND_SPADE).build(),
			RankType.BASIC,
			"Fight opponents. Last man standing wins.",
			2,
			40
	),

	FFA(
			"Free For All", "FFA",
			new ItemBuilder(Material.DIAMOND_SPADE).build(),
			RankType.BASIC,
			"Fight opponents. Last man standing wins.",
			2,
			40,
			new ArrayList<>()
	),

	SUMO(
			new ItemBuilder(Material.LEASH).build(),
			RankType.LEGEND,
			"Fight opponents on a platform. Last man standing wins.",
			2,
			40,
			Arrays.asList("SpectatorLobby", "Lobby", "ArenaLocation1", "ArenaLocation2")
	);

	private final String name;
	private final String shortName;

	private final String description;

	private final int minPlayers;
	private final int maxPlayers;

	private boolean randomLocations = false;

	private final ItemStack icon;

	private final List<String> requiredLocations;

	private final RankType rankType;

	GameType(ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.icon = icon;
		this.description = description;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.randomLocations = true;
		this.rankType = rankType;
		this.requiredLocations = new ArrayList<>(); //Random
	}

	GameType(ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.description = description;
		this.icon = icon;
		this.rankType = rankType;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations) {
		this.name = name;
		this.shortName = name;
		this.description = description;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.icon = icon;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, String shortName, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations) {
		this.name = name;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.shortName = shortName;
		this.description = description;
		this.icon = icon;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}
	
}
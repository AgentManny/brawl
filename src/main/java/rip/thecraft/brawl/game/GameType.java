package rip.thecraft.brawl.game;

import gg.manny.streamline.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.kit.type.RankType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum GameType {

	FFA(
			"Free For All", "FFA",
			ChatColor.AQUA,
			new ItemBuilder(Material.DIAMOND_SWORD).build(),
			RankType.SILVER,
			"Kill each other until one player is declared the champion.",
			5,
			50,
			new ArrayList<>(), 150, 10
	),

	SPLEEF(
			ChatColor.BLUE,
			new ItemBuilder(Material.DIAMOND_SPADE).build(),
			RankType.SILVER,
			"Destroy blocks below other players, allowing them to fall off the playing field.",
			2,
			40,
			150, 10
	),

	BRACKETS(
			ChatColor.GOLD,
			new ItemBuilder(Material.MUSHROOM_SOUP).build(),
			RankType.GOLD,
			"A series of 1v1 matches against players, whoever has the best skills wins.",
			5,
			50,
			Arrays.asList("SpectatorLobby", "Lobby", "ArenaLocation1", "ArenaLocation2"),
			100,
			10
	),

	SUMO(
			ChatColor.YELLOW,
			new ItemBuilder(Material.LEASH).build(),
			RankType.PLATINUM,
			"Punch opponents off a platform, being the last player standing to be declared victorious.",
			5,
			50,
			Arrays.asList("SpectatorLobby", "Lobby", "ArenaLocation1", "ArenaLocation2"),
			100,
			10
	),

	OITC(
			"One in the Chamber", "OITC",
			ChatColor.LIGHT_PURPLE,
			new ItemBuilder(Material.BOW).build(),
			RankType.PLATINUM,
			"Sniper. One shot, one kill! Last man standing wins!",
			5,
			50,
			new ArrayList<>(),
			150,
			15
	),

	WOOL_SHUFFLE(
			"Wool Shuffle",
			ChatColor.LIGHT_PURPLE,
			new ItemBuilder(Material.WOOL).data((byte)10).build(),
			RankType.DIAMOND,
			"Run to the right color before the floor drops! Last man standing wins.",
			2,
			50,
			Arrays.asList("Lobby", "Pos1", "Pos2"),
			100,
			15
	),

	THIMBLE(
			"Thimble",
			ChatColor.AQUA,
			new ItemBuilder(Material.WATER_BUCKET).build(),
			RankType.MASTER,
			"Jump in the water and avoid obstacles. Last man standing wins.",
			2,
			50,
			Arrays.asList("Lobby", "Jump", "Platform1", "Platform2", "JumpPlatform1", "JumpPlatform2"),
			100,
			15
	),

	TNT_TAG("TNT Tag", "Tag",
			ChatColor.RED,
			new ItemBuilder(Material.TNT).build(),
			RankType.CHAMPION,
			"Tag with a twist, don't blow up! Last man standing wins!",
			5,
			25,
			Arrays.asList("Lobby"),
			100,
			15
	),

	ARCADE("Arcade",
			ChatColor.DARK_PURPLE,
			new ItemBuilder(Material.IRON_CHESTPLATE).build(),
			RankType.CHAMPION,
			"Free for All with a twist. Players spawn with random kits.",
			5,
			50,
			Arrays.asList("Lobby"),
			100,
			15
	),

	WOOL("Wool FFA",
			ChatColor.WHITE,
			new ItemBuilder(Material.WOOL).build(),
			RankType.CHAMPION,
			"Free for All with a twist. Players spawn with the ability to place blocks.",
			5,
			50,
			Arrays.asList("Lobby"),
			100,
			15
	),

	FEAST("Feast",
			ChatColor.GREEN,
			new ItemBuilder(Material.ENCHANTMENT_TABLE).build(),
			RankType.CHAMPION,
			"Feast",
			5,
			50,
			Arrays.asList("Lobby"),
			100,
			15,
			true
	);

	private final String name;
	private final String shortName;

	private ChatColor color = ChatColor.LIGHT_PURPLE;

	private final String description;

	private final int minPlayers;
	private final int maxPlayers;

	private final int creditsReward, expReward;

	private boolean randomLocations = false, hidden = false;

	private final ItemStack icon;

	private final List<String> requiredLocations;

	private final RankType rankType;

	@Setter private boolean disabled = false;

	GameType(ChatColor color, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, int creditsReward, int expReward) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.color = color;
		this.icon = icon;
		this.description = description;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.creditsReward = creditsReward;
		this.expReward = expReward;
		this.randomLocations = true;
		this.rankType = rankType;
		this.requiredLocations = new ArrayList<>(); //Random
	}

	GameType(ChatColor color, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations, int creditsReward, int expReward) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		this.color = color;
		this.description = description;
		this.icon = icon;
		this.rankType = rankType;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.creditsReward = creditsReward;
		this.expReward = expReward;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, ChatColor color, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations, int creditsReward, int expReward) {
		this.name = name;
		this.shortName = name;
		this.color = color;
		this.description = description;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.creditsReward = creditsReward;
		this.expReward = expReward;
		this.icon = icon;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
	}

	GameType(String name, ChatColor color, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations, int creditsReward, int expReward, boolean hidden) {
		this.name = name;
		this.shortName = name;
		this.color = color;
		this.description = description;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.creditsReward = creditsReward;
		this.expReward = expReward;
		this.icon = icon;
		this.rankType = rankType;
		this.requiredLocations = requiredLocations;
		if (this.requiredLocations.isEmpty()) {
			this.randomLocations = true;
		}
		this.hidden = hidden;
	}//temporary

	GameType(String name, String shortName, ChatColor color, ItemStack icon, RankType rankType, String description, int minPlayers, int maxPlayers, List<String> requiredLocations, int creditsReward, int expReward) {
		this.name = name;
		this.color = color;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.creditsReward = creditsReward;
		this.expReward = expReward;
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
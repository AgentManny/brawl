package gg.manny.brawl.game;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public enum GameType {

	SPLEEF;
	
	@Getter
	private final String name;
	
	@Getter
	private final String shortName;
	
	@Getter
	private final List<String> requiredLocations;
	
	GameType() {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		
		this.requiredLocations = new ArrayList<>(); //Random
	}
	
	GameType(List<String> requiredLocations) {
		this.name = WordUtils.capitalizeFully(this.name()).replace("_", " ");
		this.shortName = this.name;
		
		this.requiredLocations = requiredLocations;
	}
	
	GameType(String name, List<String> requiredLocations) {
		this.name = name;
		this.shortName = name;
		
		this.requiredLocations = requiredLocations;
	}
	
	GameType(String name, String shortName, List<String> requiredLocations) {
		this.name = name;
		this.shortName = shortName;
		
		this.requiredLocations = requiredLocations;
	}
	
}
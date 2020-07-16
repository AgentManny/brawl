package rip.thecraft.brawl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum PlayerState {

    SPAWN("Spawn", ChatColor.GREEN),
    GAME_LOBBY("Game Lobby", ChatColor.LIGHT_PURPLE),
    GAME("Game", ChatColor.DARK_PURPLE),
    ARENA("Duel Arena", ChatColor.GOLD),
    MATCH("Duel Arena (Fighting)", ChatColor.RED),
    KIT_SELECTED("Warzone", ChatColor.RED),
    SPECTATING("Spectating", ChatColor.GRAY),
    FIGHTING("Warzone", ChatColor.RED);

    private String displayName;
    private ChatColor color;

}

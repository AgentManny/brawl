package rip.thecraft.brawl.player.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import rip.thecraft.brawl.player.PlayerState;

@Getter
public class PlayerKillEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private PlayerState state;
    private Player victim;

    public PlayerKillEvent(Player player, PlayerState state, Player victim) {
        super(player);

        this.player = player;
        this.state = state;
        this.victim = victim;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

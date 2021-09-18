package rip.thecraft.brawl.kit.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import rip.thecraft.brawl.kit.Kit;

@Getter
public class KitActivateEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Kit kit;

	public KitActivateEvent(Player player, Kit kit) {
		super(player);
		
		this.player = player;
		this.kit = kit;

	}

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
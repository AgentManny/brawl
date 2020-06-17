package rip.thecraft.brawl.game.lobby;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Getter
public class GamePlayerInvite {

    private UUID sender;
    private UUID target;
    private long sent;
    
    public static GamePlayerInvite create(UUID sender, UUID target) {
        return new GamePlayerInvite(sender, target, System.currentTimeMillis());
    }

    public int getLifetime() {
        return (int) (System.currentTimeMillis() - sent) / 1000;
    }

    public boolean isValid() {
        return this.getLifetime() <= 30;
    }

}
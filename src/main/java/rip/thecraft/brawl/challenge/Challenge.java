package rip.thecraft.brawl.challenge;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class Challenge<T> {

    protected final String name;
    protected Map<String, T> data = new HashMap<>();

    public abstract boolean validate(PlayerChallenge challenge);

    public abstract void complete(Player player, PlayerChallenge challenge);

}

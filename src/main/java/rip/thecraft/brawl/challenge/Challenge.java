package rip.thecraft.brawl.challenge;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class Challenge<T> {

    private final String name;
    protected Map<String, T> data = new HashMap<>();

    public abstract boolean validate(PlayerChallenge challenge);

}

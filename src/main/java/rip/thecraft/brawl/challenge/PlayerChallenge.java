package rip.thecraft.brawl.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PlayerChallenge {

    private List<Challenge> dailyChallenges = new ArrayList<>();
    private List<Challenge> weeklyChallenge = new ArrayList<>();

    private Map<String, Integer> data = new HashMap<>();

}

package rip.thecraft.brawl.challenge.type;

import rip.thecraft.brawl.challenge.Challenge;
import rip.thecraft.brawl.challenge.PlayerChallenge;

public abstract class IntegerChallenge extends Challenge<Integer> {

    private int maxValue;

    public IntegerChallenge(String name, int maxValue) {
        super(name);

        this.maxValue = maxValue;
        data.put("max-value", maxValue);
    }

    @Override
    public boolean validate(PlayerChallenge challenge) {
//        Map<String, Object> data = challenge.getData();
//        if (data != null) {
//            int currentValue = (int) data.getOrDefault("value", 0);
//            if (currentValue >= maxValue) {
//                return true;
//            }
//        }
        return false;
    }
}

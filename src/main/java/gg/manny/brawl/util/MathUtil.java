package gg.manny.brawl.util;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static long convertSecondstoTicks(int seconds) {
        return seconds * 20L;
    }
}

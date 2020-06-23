package rip.thecraft.brawl.util;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static int getRandomInt(int min) {
        return ThreadLocalRandom.current().nextInt(min, 100);
    }

    public static int getRandomProbability() {
        return ThreadLocalRandom.current().nextInt(0, 100);
    }

    public static long convertSecondstoTicks(int seconds) {
        return seconds * 20L;
    }

    /**
     * Round a number to two decimal points
     * @param d - The number to round
     * @param decimalPlaces - The amount of decimal places to round to
     * @return The rounded number
     */
    public static final double round(double d, int decimalPlaces){
        String format = "#.";
        for(int x = 1; x <= decimalPlaces; x++){
            format = format + "#";
        }
        DecimalFormat form = new DecimalFormat(format);
        return Double.parseDouble(form.format(d));
    }

    /**
     * Get the percent of two integers
     * @param n - The first integer
     * @param v - The second integer
     * @return The percent out of 100
     */
    public static final double getPercent(double n, double v){
        return round(((n * 100) / v), 1);
    }



}

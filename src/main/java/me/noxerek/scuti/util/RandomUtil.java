package me.noxerek.scuti.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author netindev
 */
public class RandomUtil {

    private static final Random RANDOM;

    static {
        RANDOM = new Random(ThreadLocalRandom.current().nextInt());
    }

    public static boolean getRandom() {
        return RANDOM.nextBoolean();
    }

    public static int getRandom(final int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int getRandom(final int min, final int max) {
        return min + RANDOM.nextInt(max - min);
    }

}

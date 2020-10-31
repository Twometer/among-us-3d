package de.twometer.amongus.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {

    private static final Random random = new Random();

    public static <T> T getRandomItem(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> Set<T> getUniqueRandom(List<T> list, int num) {
        var set = new HashSet<T>();
        do {
            set.add(getRandomItem(list));
        } while (set.size() < num);
        return set;
    }

    public static int nextInt(int low, int high) {
        return low + random.nextInt(high - low);
    }

}

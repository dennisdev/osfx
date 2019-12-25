package dev.dennis.osfx.util;

public class MathUtil {
    public static int getNextPowerOfTwo(int n) {
        int highestOneBit = Integer.highestOneBit(n);
        if (n == highestOneBit) {
            return n;
        }
        return highestOneBit << 1;
    }
}

package dev.dennis.osfx.util;

import java.math.BigInteger;

public class MathUtil {
    public static int getNextPowerOfTwo(int n) {
        int highestOneBit = Integer.highestOneBit(n);
        if (n == highestOneBit) {
            return n;
        }
        return highestOneBit << 1;
    }

    public static BigInteger modInverse(BigInteger val, int bits) {
        BigInteger shift = BigInteger.ONE.shiftLeft(bits);
        return val.modInverse(shift);
    }

    public static int modInverse(int val) {
        return modInverse(BigInteger.valueOf(val), 32).intValue();
    }

    public static long modInverse(long val) {
        return modInverse(BigInteger.valueOf(val), 64).longValue();
    }
}

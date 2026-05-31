package com.swiftcart.util;

import java.security.SecureRandom;
import java.time.Instant;

public final class TransactionIdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private TransactionIdGenerator() {
    }

    public static String next() {
        return "TXN-" + Instant.now().toEpochMilli() + "-" + RANDOM.nextInt(100_000, 999_999);
    }
}

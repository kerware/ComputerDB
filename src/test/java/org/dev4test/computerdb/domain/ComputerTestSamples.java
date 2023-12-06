package org.dev4test.computerdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ComputerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Computer getComputerSample1() {
        return new Computer().id(1L).name("name1").hardware(1).software(1);
    }

    public static Computer getComputerSample2() {
        return new Computer().id(2L).name("name2").hardware(2).software(2);
    }

    public static Computer getComputerRandomSampleGenerator() {
        return new Computer()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .hardware(intCount.incrementAndGet())
            .software(intCount.incrementAndGet());
    }
}

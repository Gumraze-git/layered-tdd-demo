package com.gumraze.demo.layeredtdd.profile.service;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomProfileTagGenerator implements ProfileTagGenerator {

    private static final char[] POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int TAG_LENGTH = 4;

    @Override
    public String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder tag = new StringBuilder(TAG_LENGTH);

        for (int i = 0; i < TAG_LENGTH; i++) {
            tag.append(POOL[random.nextInt(POOL.length)]);
        }

        return tag.toString();
    }
}

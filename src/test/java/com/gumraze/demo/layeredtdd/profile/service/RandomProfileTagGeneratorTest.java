package com.gumraze.demo.layeredtdd.profile.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomProfileTagGeneratorTest {

    private final RandomProfileTagGenerator tagGenerator = new RandomProfileTagGenerator();

    @Test
    @DisplayName("생성된 태그는 4자리이다.")
    void generate_returnsFourCharactersTag() {
        // when
        String tag = tagGenerator.generate();

        // then
        assertEquals(4, tag.length());
    }

    @Test
    @DisplayName("생성된 태그는 대문자 영어와 숫자로만 구성된다.")
    void generate_returnsUppercaseLettersAndDigitsOnly() {
        // when
        String tag = tagGenerator.generate();

        // then
        assertTrue(tag.matches("[A-Z0-9]{4}"));
    }
}

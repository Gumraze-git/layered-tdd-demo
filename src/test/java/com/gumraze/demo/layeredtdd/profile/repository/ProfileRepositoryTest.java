package com.gumraze.demo.layeredtdd.profile.repository;

import com.gumraze.demo.layeredtdd.profile.constants.AgeGroup;
import com.gumraze.demo.layeredtdd.profile.constants.Gender;
import com.gumraze.demo.layeredtdd.profile.constants.Grade;
import com.gumraze.demo.layeredtdd.profile.constants.Region;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("존재하는 이메일이면 existsByEmail은 true를 반환한다.")
    void existsByEmail_existingEmail_returnsTrue() {
        // given
        Profile profile = createProfile("myEmail@email.com", "AB12");
        profileRepository.saveAndFlush(profile);

        // when
        boolean result = profileRepository.existsByEmail("myEmail@email.com");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 existsByEmail은 false를 반환한다.")
    void existsByEmail_nonExistingEmail_returnsFalse() {
        // when
        boolean result = profileRepository.existsByEmail("nonExistedEmail@email.com");

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("존재하는 태그이면 existsByTag는 true를 반환한다.")
    void existsByTag_existingTag_returnsTrue() {
        // given
        Profile profile = createProfile("myEmail@email.com", "AB12");
        profileRepository.saveAndFlush(profile);

        // when
        boolean result = profileRepository.existsByTag("AB12");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("존재하지 않는 태그이면 existsByTag는 false를 반환한다.")
    void existsByTag_nonExistingTag_returnsFalse() {
        // when
        boolean result = profileRepository.existsByTag("ZZ99");

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("프로필 저장 시 id, createdAt, updatedAt이 저장된다.")
    void save_profile_persistsAuditFields() {
        // given
        Profile profile = createProfile("email3@test.com", "EF56");

        // when
        Profile savedProfile = profileRepository.saveAndFlush(profile);

        // then
        assertNotNull(savedProfile.getId());
        assertNotNull(savedProfile.getCreatedAt());
        assertNotNull(savedProfile.getUpdatedAt());
    }

    private Profile createProfile(String email, String tag) {
        return Profile.create(
                email,
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                tag,
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );
    }

}

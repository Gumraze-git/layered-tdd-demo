package com.gumraze.demo.layeredtdd.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.gumraze.demo.layeredtdd.profile.constants.AgeGroup;
import com.gumraze.demo.layeredtdd.profile.constants.Gender;
import com.gumraze.demo.layeredtdd.profile.constants.Grade;
import com.gumraze.demo.layeredtdd.profile.constants.Region;
import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import com.gumraze.demo.layeredtdd.profile.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    @DisplayName("유효한 입력이면 프로필 생성에 성공한다")
    void createProfile_validInput_success() {
        // given
        CreateProfileRequest request = new CreateProfileRequest(
                "myEmail@email.com",
                "myNickname",
                "myPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        given(profileRepository.save(any(Profile.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Profile profile = profileService.create(request);

        // then
        assertEquals("myEmail@email.com", profile.getEmail());
        assertEquals("myNickname", profile.getNickname());
        assertEquals("myPassword", profile.getPasswordHash());
        assertEquals("myProfileImageUrl", profile.getProfileImageUrl());
        assertEquals(Region.SEOUL, profile.getRegion());
        assertEquals(Grade.D, profile.getGrade());
        assertEquals(AgeGroup.TWENTIES, profile.getAgeGroup());
        assertEquals(Gender.MALE, profile.getGender());

        then(profileRepository).should().save(any(Profile.class));
    }

    @Test
    @DisplayName("중복 이메일이면 프로필 생성에 실패한다.")
    void createProfile_duplicateEmail_fail() {
        // given: 등록된 이메일이 포함된 요청이 주어짐
        String duplicatedEmail = "myEmail@email.com";

        CreateProfileRequest request = new CreateProfileRequest(
                duplicatedEmail,
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        given(profileRepository.existsByEmail(duplicatedEmail))
                .willReturn(true);

        // when & then
        // create 실행 시, IllegalArgumentException이 발생함.
        assertThrows(IllegalArgumentException.class, () -> profileService.create(request));
    }
}

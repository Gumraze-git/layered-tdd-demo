package com.gumraze.demo.layeredtdd.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
        String validEmail = "myEmail@email.com";
        CreateProfileRequest request = createRequest(validEmail);

        given(profileRepository.save(any(Profile.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Profile profile = profileService.create(request);

        // then
        assertEquals(validEmail, profile.getEmail());
        assertEquals("myNickname", profile.getNickname());
        assertEquals("HashedPassword", profile.getPasswordHash());
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

        CreateProfileRequest request = createRequest(duplicatedEmail);

        given(profileRepository.existsByEmail(duplicatedEmail))
                .willReturn(true);

        // when & then
        // create 실행 시, IllegalArgumentException이 발생함.
        assertThrows(IllegalArgumentException.class, () -> profileService.create(request));
    }

    @Test
    @DisplayName("중복 이메일이면 save()가 호출되지 않는다.")
    void createProfile_duplicateEmail_doNotSave() {
        // given
        String duplicatedEmail = "myEmail@email.com";
        CreateProfileRequest request = createRequest(duplicatedEmail);

        given(profileRepository.existsByEmail(duplicatedEmail))
                .willReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> profileService.create(request));

        then(profileRepository).should(never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("중복 이메일 예외 메시지가 올바르다.")
    void createProfile_duplicateEmail_exceptionMessage() {
        // given
        String duplicatedEmail = "myEmail@email.com";
        CreateProfileRequest request = createRequest(duplicatedEmail);

        given(profileRepository.existsByEmail(duplicatedEmail))
                .willReturn(true);

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
    }
    @Test
    @DisplayName("이메일이 null이면 프로필 생성에 실패한다.")
    void createProfile_nullEmail_fail() {
        // given
        CreateProfileRequest request = new CreateProfileRequest(
                null,
                "myNickname",
                "myPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when & then
        assertThrows(IllegalArgumentException.class, () -> profileService.create(request));
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 프로필 생성에 실패한다.")
    void createProfile_blankEmail_fail() {
        // given
        String emptyEmail = "";
        CreateProfileRequest request = createRequest(emptyEmail);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> profileService.create(request));

        // then
        assertEquals("이메일은 필수입니다.", exception.getMessage());

    }

    private CreateProfileRequest createRequest(String email) {
        return new CreateProfileRequest(
                email,
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );
    }
}
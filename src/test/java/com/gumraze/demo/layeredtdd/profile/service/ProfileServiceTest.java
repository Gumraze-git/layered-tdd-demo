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

    @Mock
    private ProfileTagGenerator profileTagGenerator;

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
        CreateProfileRequest request = createRequest(
                null,
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("이메일은 필수입니다.", exception.getMessage());
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

    @Test
    @DisplayName("닉네임이 null이면 프로필 생성에 실패한다.")
    void createProfile_nullNickname_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                null,
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("닉네임은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("닉네임이 빈 문자열이면 프로필 생성에 실패한다.")
    void createProfile_blankNickname_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("닉네임은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 해시가 null이면 프로필 생성에 실패한다.")
    void createProfile_nullPasswordHash_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                null,
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("비밀번호는 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 해시가 빈 문자열이면 프로필 생성에 실패한다.")
    void createProfile_blankPasswordHash_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                "",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("비밀번호는 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("지역이 null이면 프로필 생성에 실패한다.")
    void createProfile_nullRegion_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                null,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("지역은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("등급이 null이면 프로필 생성에 실패한다.")
    void createProfile_nullGrade_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                null,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("등급은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("연령대가 null이면 프로필 생성에 실패한다.")
    void createProfile_nullAgeGroup_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                null,
                Gender.MALE
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("연령대는 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("성별이 null이면 프로필 생성에 실패한다.")
    void createProfile_nullGender_fail() {
        // given
        CreateProfileRequest request = createRequest(
                "myEmail@email.com",
                "myNickname",
                "HashedPassword",
                "myProfileImageUrl",
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                null
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileService.create(request)
        );

        // then
        assertEquals("성별은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("프로필 생성 시 4자리 태그가 자동 생성되어 할당된다.")
    void createProfile_assignGeneratedTag() {
        // given
        String validEmail = "myEmail@email.com";
        String generatedTag = "AB12";
        CreateProfileRequest request = createRequest(validEmail);

        // generate가 태그 생성
        given(profileTagGenerator.generate()).willReturn(generatedTag);
        // 생성된 태그는 아직 DB에 없어야함
        given(profileRepository.existsByTag(generatedTag)).willReturn(false);

        given(profileRepository.save(any(Profile.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Profile profile = profileService.create(request); // 여기서 태그가 저장이 되어야함

        // then
        assertEquals(generatedTag, profile.getTag());
    }

    @Test
    @DisplayName("이미 존재하는 태그가 생성되면, 다시 새로운 태그를 생성한다.")
    void createProfile_duplicateTag_regenerateTag() {
        // given
        String validEmail = "myEmail@email.com";
        String duplicatedTag = "AB12"; // 첫 번째로 생성한 태그가 이미 중복인 경우
        String regeneratedTag = "CD34"; // 태그를 다시 생성함
        CreateProfileRequest request = createRequest(validEmail);

        given(profileTagGenerator.generate()).willReturn(duplicatedTag, regeneratedTag);
        given(profileRepository.existsByTag(duplicatedTag)).willReturn(true);
        given(profileRepository.existsByTag(regeneratedTag)).willReturn(false);
        given(profileRepository.save(any(Profile.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Profile profile = profileService.create(request);

        // then
        assertEquals(regeneratedTag, profile.getTag());
    }



    private CreateProfileRequest createRequest(String email) {
        return createRequest(
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

    private CreateProfileRequest createRequest(
            String email,
            String nickname,
            String passwordHash,
            String profileImageUrl,
            Region region,
            Grade grade,
            AgeGroup ageGroup,
            Gender gender
    ) {
        return new CreateProfileRequest(
                email,
                nickname,
                passwordHash,
                profileImageUrl,
                region,
                grade,
                ageGroup,
                gender
        );
    }
}

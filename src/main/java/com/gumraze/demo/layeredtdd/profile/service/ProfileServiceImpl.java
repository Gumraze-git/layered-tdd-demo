package com.gumraze.demo.layeredtdd.profile.service;

import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import com.gumraze.demo.layeredtdd.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileTagGenerator profileTagGenerator;

    @Override
    public Profile create(CreateProfileRequest request) {
        validateRequiredFields(request);
        validateDuplicateEmail(request.email());

        String tag = generateUniqueTag();

        Profile profile = Profile.create(
                request.email(),
                request.nickname(),
                request.passwordHash(),
                request.profileImageUrl(),
                tag,
                request.region(),
                request.grade(),
                request.ageGroup(),
                request.gender()
        );

        return profileRepository.save(profile);
    }

    private void validateRequiredFields(CreateProfileRequest request) {
        validateText(request.email(), "이메일은 필수입니다.");
        validateText(request.nickname(), "닉네임은 필수입니다.");
        validateText(request.passwordHash(), "비밀번호는 필수입니다.");
        validateRequired(request.region(), "지역은 필수입니다.");
        validateRequired(request.grade(), "등급은 필수입니다.");
        validateRequired(request.ageGroup(), "연령대는 필수입니다.");
        validateRequired(request.gender(), "성별은 필수입니다.");
    }

    private void validateDuplicateEmail(String email) {
        if (profileRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    private void validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateRequired(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private String generateUniqueTag() {
        do {
            String tag = profileTagGenerator.generate();
            if (!profileRepository.existsByTag(tag)) {
                return tag;
            }
        } while (true);
    }

}

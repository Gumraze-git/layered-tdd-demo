package com.gumraze.demo.layeredtdd.profile.service;

import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import com.gumraze.demo.layeredtdd.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile create(CreateProfileRequest request) {
        Profile profile = Profile.create(
                request.email(),
                request.nickname(),
                request.passwordHash(),
                request.profileImageUrl(),
                request.region(),
                request.grade(),
                request.ageGroup(),
                request.gender()
        );

        return profileRepository.save(profile);
    }
}

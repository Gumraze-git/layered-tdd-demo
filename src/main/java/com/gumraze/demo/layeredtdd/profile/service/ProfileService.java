package com.gumraze.demo.layeredtdd.profile.service;

import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;

public interface ProfileService {
    Profile create(CreateProfileRequest request);
}

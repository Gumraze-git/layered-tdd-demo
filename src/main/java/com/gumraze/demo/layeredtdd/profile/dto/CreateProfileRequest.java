package com.gumraze.demo.layeredtdd.profile.dto;

import com.gumraze.demo.layeredtdd.profile.constants.AgeGroup;
import com.gumraze.demo.layeredtdd.profile.constants.Gender;
import com.gumraze.demo.layeredtdd.profile.constants.Grade;
import com.gumraze.demo.layeredtdd.profile.constants.Region;

public record CreateProfileRequest(
        String email,
        String nickname,
        String passwordHash,
        String profileImageUrl,
        Region region,
        Grade grade,
        AgeGroup ageGroup,
        Gender gender
) { }
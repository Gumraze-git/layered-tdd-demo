package com.gumraze.demo.layeredtdd.profile.entity;

import com.gumraze.demo.layeredtdd.profile.constants.AgeGroup;
import com.gumraze.demo.layeredtdd.profile.constants.Gender;
import com.gumraze.demo.layeredtdd.profile.constants.Grade;
import com.gumraze.demo.layeredtdd.profile.constants.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    private String profileImageUrl;
    private String tag;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Profile() {}

    public static Profile create(
            String email,
            String nickname,
            String passwordHash,
            String profileImageUrl,
            Region region,
            Grade grade,
            AgeGroup ageGroup,
            Gender gender
    ) {
        Profile profile = new Profile();
        profile.email = email;
        profile.nickname = nickname;
        profile.passwordHash = passwordHash;
        profile.profileImageUrl = profileImageUrl;
        profile.region = region;
        profile.grade = grade;
        profile.ageGroup = ageGroup;
        profile.gender = gender;
        return profile;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

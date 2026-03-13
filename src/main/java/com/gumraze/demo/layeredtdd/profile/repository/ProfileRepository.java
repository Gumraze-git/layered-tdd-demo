package com.gumraze.demo.layeredtdd.profile.repository;

import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByEmail(String email);

    boolean existsByTag(String tag);
}

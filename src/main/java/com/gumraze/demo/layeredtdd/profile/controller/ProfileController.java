package com.gumraze.demo.layeredtdd.profile.controller;

import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateProfileRequest request
    ) {
        profileService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

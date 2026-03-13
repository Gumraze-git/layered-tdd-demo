package com.gumraze.demo.layeredtdd.profile.controller;

import com.gumraze.demo.layeredtdd.profile.constants.AgeGroup;
import com.gumraze.demo.layeredtdd.profile.constants.Gender;
import com.gumraze.demo.layeredtdd.profile.constants.Grade;
import com.gumraze.demo.layeredtdd.profile.constants.Region;
import com.gumraze.demo.layeredtdd.profile.dto.CreateProfileRequest;
import com.gumraze.demo.layeredtdd.profile.entity.Profile;
import com.gumraze.demo.layeredtdd.profile.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest()
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService profileService;

    @Test
    @DisplayName("유효한 요청이면 201과 프로필 응답을 반환한다.")
    void createProfile_validRequest_returnCreated() throws Exception {
        // given
        String email = "myEmail@email.com";
        String nickname = "myNickname";
        String password = "HashedPassword";
        String profileImageUrl = "myProfileImageUrl";
        String tag = "AB12";

        CreateProfileRequest request = new CreateProfileRequest(
                email,
                nickname,
                password,
                profileImageUrl,
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        String requestBody = objectMapper.writeValueAsString(request);

        Profile profile = Profile.create(
                email,
                nickname,
                password,
                profileImageUrl,
                tag,
                Region.SEOUL,
                Grade.D,
                AgeGroup.TWENTIES,
                Gender.MALE
        );

        given(profileService.create(any())).willReturn(profile);

        // when & then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // 실제 서비스 호출이 되었는지 추가 검증
        then(profileService).should().create(any(CreateProfileRequest.class));
    }

    @Test
    @DisplayName("잘못된 region 값이면 400을 반환한다.")
    void createProfile_invalidRegion_returnsBadRequest() throws Exception {
        // given
        String requestBody = """
            {
              "email": "myEmail@email.com",
              "nickname": "myNickname",
              "passwordHash": "HashedPassword",
              "profileImageUrl": "myProfileImageUrl",
              "region": "INVALID",
              "grade": "D",
              "ageGroup": "TWENTIES",
              "gender": "MALE"
            }
            """;

        // when & then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일이 없으면 400을 반환한다.")
    void createProfile_nullEmail_returnsBadRequest() throws Exception {
        // given
        String requestBody = """
                    {
                      "nickname": "myNickname",
                      "passwordHash": "HashedPassword",
                      "profileImageUrl": "myProfileImageUrl",
                      "region": "SEOUL",
                      "grade": "D",
                      "ageGroup": "TWENTIES",
                """;
        // when & then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복 이메일이면 409을 반환한다.")
    void createProfile_duplicateEmail_returnsConflict() throws Exception {
        // given
        String requestBody = """
                {
                  "email": "myEmail@email.com",
                  "nickname": "myNickname",
                  "passwordHash": "HashedPassword",
                  "profileImageUrl": "myProfileImageUrl",
                  "region": "SEOUL",
                  "grade": "D",
                  "ageGroup": "TWENTIES",
                  "gender": "MALE"
                }
                """;

        given(profileService.create(any(CreateProfileRequest.class)))
                .willThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."));

        // when & then
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
}



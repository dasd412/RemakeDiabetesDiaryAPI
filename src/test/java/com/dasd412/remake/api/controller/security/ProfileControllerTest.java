/*
 * @(#)ProfileControllerTest.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.TestUserDetailsService;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodDTO;
import com.dasd412.remake.api.controller.security.profile.dto.PasswordUpdateRequestDTO;
import com.dasd412.remake.api.controller.security.profile.dto.ProfileUpdateRequestDTO;
import com.dasd412.remake.api.controller.security.domain_view.dto.ProfileResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.profile.ProfileRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProfileControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final TestUserDetailsService testUserDetailsService = new TestUserDetailsService();

    private PrincipalDetails principalDetails;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setup() throws Exception {
        logger.info("set up");

        mockMvc = applySpringSecurity();

        Writer me = makeWriter();
        principalDetails = saveSession(me);

        SecurityDiaryPostRequestDTO dto = makePostRequestDTO();

        //when and then
        requestPostForSetUp(dto);
    }

    private MockMvc applySpringSecurity() {
        return MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private Writer makeWriter() {
        return Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(TestUserDetailsService.USERNAME)
                .email(TestUserDetailsService.USERNAME)
                .password("test")
                .role(Role.User)
                .provider(null)
                .providerId(null)
                .build();
    }

    private PrincipalDetails saveSession(Writer writer) {
        writerRepository.save(writer);
        principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestUserDetailsService.USERNAME);
        return principalDetails;
    }

    private SecurityDiaryPostRequestDTO makePostRequestDTO() {
        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i))
                .collect(Collectors.toList());

        return SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(0).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .breakFastSugar(0).lunchSugar(0).dinnerSugar(0)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();
    }

    private void requestPostForSetUp(SecurityDiaryPostRequestDTO dto) throws Exception {
        mockMvc.perform(post("/api/diary/user/diabetes-diary").with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }


    @After
    public void clean() {
        logger.info("clean\n");
        writerRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    public void existProfileAfterSetUp() throws Exception {
        //given
        String url = "/profile/view";

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        matchesSetUpWriter();
        existDefaultProfile();
    }

    private void matchesSetUpWriter(){
        List<Writer> writers = writerRepository.findAll();
        assertThat(writers.size()).isEqualTo(1);
        assertThat(writers.get(0).getProfile().getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);
    }

    private void existDefaultProfile() {
        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList.size()).isEqualTo(1);
        assertThat(profileList.get(0).getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);
    }

    @Test
    public void findProfile() throws Exception {
        //given
        String url = "/profile/view";
        //처음에 프로필이 없을 경우엔 make profile (기본 당뇨 단계는 NORMAL)
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        //when and then (프로필이 있을 경우엔 find profile)
        MvcResult result = mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andReturn();

        ProfileResponseDTO dto = (ProfileResponseDTO) Objects.requireNonNull(result.getModelAndView()).getModel().get("profile");
        assertThat(dto.getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);
    }

    @Test
    public void updateProfile() throws Exception {
        //given
        mockMvc.perform(get("/profile/view").with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String url = "/profile/info";
        ProfileUpdateRequestDTO dto = new ProfileUpdateRequestDTO(DiabetesPhase.DIABETES);

        //when and then
        mockMvc.perform(put(url).with(user(principalDetails))
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.diabetesPhase").value(DiabetesPhase.DIABETES.name()));

    }

    /*
    회원 탈퇴 테스트
     */
    @Test
    public void withDraw() throws Exception {
        //given
        String url = "/profile/withdrawal";

        //when and then
        mockMvc.perform(delete(url).with(user(principalDetails)))
                .andExpect(status().isOk());
    }

    @Test
    public void changePasswordOfOAuthUser() throws Exception {
        //given
        Writer oAuthUser = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("TEST-NAME")
                .email("test@google.com")
                .provider("google")
                .providerId("1")
                .password(null)
                .role(Role.User)
                .build();

        writerRepository.save(oAuthUser);
        PrincipalDetails oAuthPrincipalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestUserDetailsService.OAUTH_USER_NAME);

        String url = "/profile/password";

        String password = "test12345678";

        PasswordUpdateRequestDTO dto = makePasswordForTest(password, password);

        //when and then
        mockMvc.perform(put(url).with(user(oAuthPrincipalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.message").value("OAuth 로그인 유저는 비밀 번호를 변경할 수 없습니다."))
                .andExpect(jsonPath("$.error.status").value(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void passwordNotConfirm() throws Exception {
        //given
        String url = "/profile/password";

        String password = "test1";
        String passwordConfirm = "test2";

        PasswordUpdateRequestDTO dto = makePasswordForTest(password, passwordConfirm);

        //when and then
        isPasswordUpdateHttpStatusBadRequest(url, dto);
    }

    @Test
    public void invalidSizePassword() throws Exception {
        //given
        String url = "/profile/password";

        String password = "test";

        PasswordUpdateRequestDTO dto = makePasswordForTest(password, password);

        //when and then
        isPasswordUpdateHttpStatusBadRequest(url, dto);
    }

    @Test
    public void updatePassword() throws Exception {
        //given
        String url = "/profile/password";
        String password = "test12345678";

        PasswordUpdateRequestDTO dto = makePasswordForTest(password, password);

        //when and then
        isPasswordUpdateHttpStatusOk(url, dto);

        Writer found = writerRepository.findAll().get(0);

        assertThat(bCryptPasswordEncoder.matches(password, found.getPassword())).isTrue();
    }

    private PasswordUpdateRequestDTO makePasswordForTest(String password, String passwordConfirm) {
        return PasswordUpdateRequestDTO.builder()
                .password(password)
                .passwordConfirm(passwordConfirm)
                .build();
    }

    private void isPasswordUpdateHttpStatusOk(String url, PasswordUpdateRequestDTO dto) throws Exception {
        mockMvc.perform(put(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    private void isPasswordUpdateHttpStatusBadRequest(String url, PasswordUpdateRequestDTO dto) throws Exception {
        mockMvc.perform(put(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}

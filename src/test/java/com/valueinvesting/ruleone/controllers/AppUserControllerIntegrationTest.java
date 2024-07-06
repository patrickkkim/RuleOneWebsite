package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.exceptions.UserNotFoundException;
import com.valueinvesting.ruleone.security.JwtUtil;
import com.valueinvesting.ruleone.security.SecurityConfig;
import com.valueinvesting.ruleone.services.AppUserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@Transactional
@WebAppConfiguration
@AutoConfigureMockMvc
class AppUserControllerIntegrationTest {

    @Autowired AppUserService appUserService;
    @Autowired EntityManager entityManager;
    @Autowired JwtUtil jwtUtil;
    @Autowired private MockMvc mockMvc;
    private AppUser appUser;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("honggildong");
        appUser.setEmail("a@a.com");
        appUser.setEncryptedPassword("asdfasdfasdf123!");
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void checkIfCreatesAppUser() throws Exception {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", "honggildong");
        userMap.put("encryptedPassword", "asdfasdfasdf123!");
        userMap.put("email", "a@a.com");
        String requestBody = objectMapper.writeValueAsString(userMap);

        String jsonResponse = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        appUser = appUserService.getAppUserByUsername("honggildong");
        AppUser newAppUser = objectMapper.readValue(jsonResponse, AppUser.class);

        assertThat(newAppUser.getId()).isEqualTo(appUser.getId());
    }

    @Test
    @WithMockUser(username = "honggildong")
    void checkIfUpdatesAppUserEmail() throws Exception {
        appUserService.createAppUser(appUser);
        AppUser requestUser = new AppUser();
        requestUser.setUsername("honggildong");
        requestUser.setEmail("b@b.com");

        String requestBody = objectMapper.writeValueAsString(requestUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.refresh(appUser);

        appUser = appUserService.getAppUserByUsername("honggildong");

        assertThat(appUser.getEmail()).isEqualTo("b@b.com");
    }

    @Test
    @WithMockUser(username = "honggildong")
    void checkIfUpdatesAppUserPassword() throws Exception {
        appUserService.createAppUser(appUser);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("encryptedPassword", "fdsafdasfdsa123!");

        String requestBody = objectMapper.writeValueAsString(requestMap);
        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.refresh(appUser);

        appUser = appUserService.getAppUserByUsername("honggildong");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches("fdsafdasfdsa123!",
                appUser.getEncryptedPassword().replace("{bcrypt}", "")))
                .isTrue();
    }

    @Test
    @WithMockUser(username = "honggildong")
    void checkIfUpdatesAppUserPasswordAndEmail() throws Exception {
        appUserService.createAppUser(appUser);
        AppUser requestUser = new AppUser();
        requestUser.setUsername("honggildong");
        requestUser.setEncryptedPassword("fdsafdasfdsa123!");
        requestUser.setEmail("b@b.com");

        String requestBody = objectMapper.writeValueAsString(requestUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        entityManager.refresh(appUser);

        appUser = appUserService.getAppUserByUsername("honggildong");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches("fdsafdasfdsa123!",
                appUser.getEncryptedPassword().replace("{bcrypt}", "")))
                .isTrue();
        assertThat(appUser.getEmail()).isEqualTo("b@b.com");
    }

    @Test
    void checkIfLogins() throws Exception {
        appUserService.createAppUser(appUser);
        Map<String, String> map = new HashMap<>();
        map.put("username", "honggildong");
        map.put("password", "asdfasdfasdf123!");

        String requestBody = objectMapper.writeValueAsString(map);

        String jwt = mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        assertThat(jwtUtil.extractUsername(jwt))
                .isEqualTo("honggildong");
    }

    @Test
    void checkIfLoginFailsWhenCredentialsAreInvalid() throws Exception {
        appUserService.createAppUser(appUser);
        Map<String, String> map = new HashMap<>();
        map.put("username", "honggildong");
        map.put("password", "asdfasdfasdf123");

        String requestBody = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void checkIfDeletesAppUser() throws Exception {
        appUserService.createAppUser(appUser);
        String jwt = jwtUtil.generateToken(appUser.getUsername());

        String response = mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                        .with(csrf())
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> appUserService.getAppUserByUsername("honggildong"));
    }

    @Test
    void checkIfDeleteAppUserThrowsExceptionWhenNotAuthenticated() throws Exception {
        appUserService.createAppUser(appUser);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
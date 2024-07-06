package com.valueinvesting.ruleone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.valueinvesting.ruleone.entities.AppUser;
import com.valueinvesting.ruleone.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(value=AppUserController.class,
        excludeAutoConfiguration={SecurityAutoConfiguration.class})
class AppUserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean private AppUserService appUserService;
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
        AppUser newAppUser = new AppUser();
        Mockito.when(appUserService.createAppUser(Mockito.any())).thenReturn(newAppUser);

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionWhenUsernameIsShort() throws Exception {
        appUser.setUsername("asdf");

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionWhenPasswordIsShort() throws Exception {
        appUser.setEncryptedPassword("asdf123!");

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void checkIfCreateAppUserThrowsExceptionWhenPasswordDoesNotIncludeSpecialCharacter() throws Exception {
        appUser.setEncryptedPassword("asdfasdfasdf123");

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "honggildong", roles = {"TRIAL"})
    void checkIfUpdatesAppUser() throws Exception {
        Mockito.when(appUserService.getAuthenticatedUser()).thenReturn(appUser);

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "honggildong", roles = {"TRIAL"})
    void checkIfUpdateAppUserThrowsExceptionWhenPasswordIsInvalid() throws Exception {
        appUser.setEncryptedPassword("asdfasdf");
        Mockito.when(appUserService.getAuthenticatedUser()).thenReturn(appUser);

        String requestBody = objectMapper.writeValueAsString(appUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void checkIfLogins() throws Exception {
        Mockito.when(appUserService.login("honggildong", "asdfasdfasdf123!"))
                .thenReturn("asdf");

        Map<String, String> map = new HashMap<>();
        map.put("username", "honggildong");
        map.put("password", "asdfasdfasdf123!");

        String requestBody = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("asdf"));
    }
}
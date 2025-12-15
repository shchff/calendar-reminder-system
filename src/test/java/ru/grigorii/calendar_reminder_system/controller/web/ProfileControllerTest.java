package ru.grigorii.calendar_reminder_system.controller.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.ReminderMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserMvcService userService;

    @MockitoBean
    private ReminderMvcService reminderMvcService;
    @BeforeEach
    void setup()
    {
        UserDto user = new UserDto(
                1L,
                "Test",
                "User",
                "user@test.com",
                null,
                "USER",
                null
        );

        when(userService.getByEmail(anyString()))
                .thenReturn(user);

        when(reminderMvcService.countActive(anyLong()))
                .thenReturn(0L);
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void profile_shouldReturnViewWithUser() throws Exception
    {
        UserDto user = new UserDto(
                1L,
                "Test",
                "User",
                "user@test.com",
                null,
                "USER",
                null
        );

        given(userService.getByEmail("user@test.com"))
                .willReturn(user);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateProfile_withoutEmailChange_shouldRedirectToProfile() throws Exception
    {
        mockMvc.perform(
                        post("/profile")
                                .with(csrf())
                                .param("name", "New")
                                .param("surname", "Name")
                                .param("email", "user@test.com")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?updated=true"));

        then(userService)
                .should()
                .updateProfile(eq("user@test.com"), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void updateProfile_withEmailChange_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(
                        post("/profile")
                                .with(csrf())
                                .param("name", "New")
                                .param("surname", "Name")
                                .param("email", "new@test.com")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?emailChanged=true"));

        then(userService)
                .should()
                .updateProfile(eq("user@test.com"), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void changePassword_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(
                        post("/profile/password")
                                .with(csrf())
                                .param("oldPassword", "old")
                                .param("newPassword", "new")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?passwordChanged=true"));

        then(userService)
                .should()
                .changePassword("user@test.com", "old", "new");
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void deleteAccount_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(
                        post("/profile/delete")
                                .with(csrf())
                                .param("password", "secret")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?accountDeleted=true"));

        then(userService)
                .should()
                .deleteAccount("user@test.com", "secret");
    }
}

package ru.grigorii.calendar_reminder_system.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.grigorii.calendar_reminder_system.dto.ReminderDto;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.ReminderMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReminderMvcService reminderService;

    @MockitoBean
    private UserMvcService userService;

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void notifications_shouldReturnViewWithReminders() throws Exception
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

        List<ReminderDto> reminders = List.of(
                new ReminderDto(
                        10L,
                        100L,
                        LocalDateTime.now(),
                        "EMAIL",
                        false,
                        "Event"
                )
        );

        when(userService.getByEmail("user@test.com"))
                .thenReturn(user);

        when(reminderService.findActive(1L))
                .thenReturn(reminders);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attributeExists("reminders"))
                .andExpect(model().attribute("reminders", reminders));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void markRead_shouldRedirectToNotifications() throws Exception
    {
        UserDto user = new UserDto(
                1L, "Test", "User",
                "user@test.com", null, "USER", null
        );

        given(userService.getByEmail("user@test.com"))
                .willReturn(user);

        mockMvc.perform(
                        post("/notifications/10/read")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        then(reminderService)
                .should()
                .markAsRead(10L, 1L);
    }

}

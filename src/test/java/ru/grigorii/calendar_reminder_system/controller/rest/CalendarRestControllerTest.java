package ru.grigorii.calendar_reminder_system.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.service.rest.CalendarRestService;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CalendarRestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ru.grigorii.calendar_reminder_system.controller.advice.NotificationAdvice.class
                }
        )
)
@EnableMethodSecurity
class CalendarRestControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();;

    @MockitoBean
    private CalendarRestService calendarService;

    private CalendarDto sampleCalendar()
    {
        return new CalendarDto(
                1L,
                "Work",
                "Work calendar",
                10L,
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturnCalendars() throws Exception
    {
        given(calendarService.findAll())
                .willReturn(List.of(sampleCalendar()));

        mockMvc.perform(get("/api/calendars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Work"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_shouldReturnCalendar() throws Exception
    {
        given(calendarService.findById(1L))
                .willReturn(sampleCalendar());

        mockMvc.perform(get("/api/calendars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_shouldReturn404() throws Exception
    {
        given(calendarService.findById(1L))
                .willThrow(new EntityNotFoundException("Calendar not found"));

        mockMvc.perform(get("/api/calendars/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreatedCalendar() throws Exception
    {
        CalendarDto input = CalendarDto.forCreate(
                "New calendar",
                "Description",
                10L
        );

        given(calendarService.create(any(CalendarDto.class)))
                .willReturn(sampleCalendar());

        mockMvc.perform(
                        post("/api/calendars")
                                .with(csrf())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_invalid_shouldReturn400() throws Exception
    {
        CalendarDto invalid = new CalendarDto(
                null,
                "",
                null,
                null,
                null
        );

        mockMvc.perform(
                        post("/api/calendars")
                                .with(csrf())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(invalid))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedCalendar() throws Exception
    {
        CalendarDto dto = sampleCalendar();

        given(calendarService.update(eq(1L), any(CalendarDto.class)))
                .willReturn(dto);

        mockMvc.perform(
                        put("/api/calendars/1")
                                .with(csrf())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn204() throws Exception
    {
        mockMvc.perform(
                        delete("/api/calendars/1")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        then(calendarService)
                .should()
                .delete(1L);
    }


    @Test
    @WithMockUser(roles = "USER")
    void accessDenied_forNonAdmin() throws Exception
    {
        mockMvc.perform(get("/api/calendars"))
                .andExpect(status().isForbidden());
    }
}

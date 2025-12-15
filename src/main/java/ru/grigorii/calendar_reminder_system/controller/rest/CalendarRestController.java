package ru.grigorii.calendar_reminder_system.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.service.rest.CalendarRestService;

import java.util.List;

/**
 * Контроллер для REST API календаря, доступен только админу
 */
@RestController
@RequestMapping("/api/calendars")
@PreAuthorize("hasRole('ADMIN')")
public class CalendarRestController
{

    private final CalendarRestService calendarService;

    public CalendarRestController(CalendarRestService calendarService)
    {
        this.calendarService = calendarService;
    }

    @GetMapping
    public List<CalendarDto> getAll()
    {
        return calendarService.findAll();
    }

    @GetMapping("/{id}")
    public CalendarDto getById(@PathVariable Long id)
    {
        return calendarService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarDto create(@RequestBody @Valid CalendarDto dto)
    {
        return calendarService.create(dto);
    }

    @PutMapping("/{id}")
    public CalendarDto update(
            @PathVariable Long id,
            @RequestBody @Valid CalendarDto dto
    )
    {
        return calendarService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        calendarService.delete(id);
    }
}

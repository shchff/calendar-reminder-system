package ru.grigorii.calendar_reminder_system.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.EventDto;
import ru.grigorii.calendar_reminder_system.service.rest.EventRestService;

import java.util.List;

/**
 * Контроллер для REST API событий, доступен только админу
 */
@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasRole('ADMIN')")
public class EventRestController
{
    private final EventRestService eventService;

    public EventRestController(EventRestService eventService)
    {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> getAll()
    {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id)
    {
        return eventService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody @Valid EventDto dto)
    {
        return eventService.create(dto);
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable Long id, @RequestBody @Valid EventDto dto)
    {
        return eventService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        eventService.delete(id);
    }
}

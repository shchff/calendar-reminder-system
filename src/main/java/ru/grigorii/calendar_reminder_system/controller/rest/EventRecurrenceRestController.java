package ru.grigorii.calendar_reminder_system.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.EventRecurrenceDto;
import ru.grigorii.calendar_reminder_system.service.rest.EventRecurrenceRestService;

import java.util.List;

/**
 * Контроллер для REST API повторений событий, доступен только админу
 */
@RestController
@RequestMapping("/api/event-recurrences")
@PreAuthorize("hasRole('ADMIN')")
public class EventRecurrenceRestController
{

    private final EventRecurrenceRestService service;

    @Autowired
    public EventRecurrenceRestController(EventRecurrenceRestService service)
    {
        this.service = service;
    }

    @GetMapping
    public List<EventRecurrenceDto> getAll()
    {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EventRecurrenceDto getById(@PathVariable Long id)
    {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRecurrenceDto create(
            @RequestBody @Valid EventRecurrenceDto dto
    )
    {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EventRecurrenceDto update(
            @PathVariable Long id,
            @RequestBody @Valid EventRecurrenceDto dto
    )
    {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        service.delete(id);
    }
}

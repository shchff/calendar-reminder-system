package ru.grigorii.calendar_reminder_system.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.ReminderDto;
import ru.grigorii.calendar_reminder_system.service.rest.ReminderRestService;

import java.util.List;

/**
 * Контроллер для REST API напоминаний, доступен только админу
 */
@RestController
@RequestMapping("/api/admin/reminders")
@PreAuthorize("hasRole('ADMIN')")
public class ReminderRestController
{

    private final ReminderRestService service;

    public ReminderRestController(ReminderRestService service)
    {
        this.service = service;
    }

    @GetMapping
    public List<ReminderDto> getAll()
    {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ReminderDto getById(@PathVariable Long id)
    {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderDto create(
            @RequestBody @Valid ReminderDto dto
    )
    {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ReminderDto update(
            @PathVariable Long id,
            @RequestBody @Valid ReminderDto dto
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

package ru.grigorii.calendar_reminder_system.controller.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.rest.UserRestService;

import java.util.List;

/**
 * Контроллер для REST API пользователей, доступен только админу
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserRestController
{
    private final UserRestService userService;

    @Autowired
    public UserRestController(UserRestService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll()
    {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id)
    {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto dto)
    {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    public UserDto update(
            @PathVariable Long id,
            @RequestBody @Valid UserDto dto
    )
    {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        userService.delete(id);
    }
}

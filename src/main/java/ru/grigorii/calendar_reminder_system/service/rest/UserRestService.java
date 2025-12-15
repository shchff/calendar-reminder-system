package ru.grigorii.calendar_reminder_system.service.rest;

import ru.grigorii.calendar_reminder_system.dto.UserDto;

import java.util.List;

public interface UserRestService
{
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(UserDto dto);
    UserDto update(Long id, UserDto dto);
    void delete(Long id);
}

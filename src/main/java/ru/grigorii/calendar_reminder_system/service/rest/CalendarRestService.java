package ru.grigorii.calendar_reminder_system.service.rest;

import ru.grigorii.calendar_reminder_system.dto.CalendarDto;

import java.util.List;

public interface CalendarRestService {

    List<CalendarDto> findAll();

    CalendarDto findById(Long id);

    List<CalendarDto> findByOwnerId(Long ownerId);

    CalendarDto create(CalendarDto dto);

    CalendarDto update(Long id, CalendarDto dto);

    void delete(Long id);
}

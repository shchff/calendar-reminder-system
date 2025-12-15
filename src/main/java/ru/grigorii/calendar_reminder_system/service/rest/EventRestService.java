package ru.grigorii.calendar_reminder_system.service.rest;

import ru.grigorii.calendar_reminder_system.dto.EventDto;

import java.util.List;

public interface EventRestService {

    List<EventDto> findAll();
    List<EventDto> findByCalendar(Long calendarId);
    EventDto findById(Long id);
    EventDto create(EventDto dto);
    EventDto update(Long id, EventDto dto);
    void delete(Long id);
}

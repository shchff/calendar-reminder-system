package ru.grigorii.calendar_reminder_system.service.rest;

import ru.grigorii.calendar_reminder_system.dto.EventRecurrenceDto;

import java.util.List;

public interface EventRecurrenceRestService
{
    List<EventRecurrenceDto> findAll();
    EventRecurrenceDto findById(Long id);
    EventRecurrenceDto findByEventId(Long eventId);
    EventRecurrenceDto create(EventRecurrenceDto dto);
    EventRecurrenceDto update(Long id, EventRecurrenceDto dto);
    void delete(Long id);
}

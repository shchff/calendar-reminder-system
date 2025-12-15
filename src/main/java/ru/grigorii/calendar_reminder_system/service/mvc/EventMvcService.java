package ru.grigorii.calendar_reminder_system.service.mvc;

import ru.grigorii.calendar_reminder_system.dto.EventDto;

import java.util.List;

public interface EventMvcService
{
    List<EventDto> findByCalendar(Long calendarId, Long ownerId);

    EventDto create(EventDto dto, Long ownerId);

    void delete(Long eventId, Long ownerId);

    void markDone(Long eventId, Long ownerId);

    void markUndone(Long eventId, Long ownerId);
}

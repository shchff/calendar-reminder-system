package ru.grigorii.calendar_reminder_system.service.mvc;

import ru.grigorii.calendar_reminder_system.dto.CalendarDto;

import java.util.List;

public interface CalendarMvcService
{

    List<CalendarDto> findByOwner(Long ownerId);

    CalendarDto findByIdForOwner(Long calendarId, Long ownerId);

    CalendarDto createCalendar(CalendarDto dto);

    void updateForOwner(Long calendarId,
                        CalendarDto dto,
                        Long ownerId);

    void deleteById(Long calendarId, Long ownerId);
}

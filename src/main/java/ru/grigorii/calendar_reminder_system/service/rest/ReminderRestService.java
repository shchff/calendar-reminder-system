package ru.grigorii.calendar_reminder_system.service.rest;

import ru.grigorii.calendar_reminder_system.dto.ReminderDto;

import java.util.List;

public interface ReminderRestService
{
    List<ReminderDto> findAll();

    ReminderDto findById(Long id);

    ReminderDto findByEventId(Long eventId);

    ReminderDto create(ReminderDto dto);

    ReminderDto update(Long id, ReminderDto dto);

    void delete(Long id);
}

package ru.grigorii.calendar_reminder_system.service.mvc;

import ru.grigorii.calendar_reminder_system.dto.ReminderDto;

import java.util.List;

public interface ReminderMvcService
{
    List<ReminderDto> findActive(Long userId);
    void markAsRead(Long reminderId, Long userId);
    long countActive(Long userId);
}

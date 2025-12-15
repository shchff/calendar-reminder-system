package ru.grigorii.calendar_reminder_system.service.exception;

import jakarta.persistence.EntityNotFoundException;

public class CalendarNotFoundException extends EntityNotFoundException
{
    public CalendarNotFoundException(Long id)
    {
        super("Calendar with id " + id + " not found");
    }
}


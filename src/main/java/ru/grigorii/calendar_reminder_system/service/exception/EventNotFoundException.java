package ru.grigorii.calendar_reminder_system.service.exception;

import jakarta.persistence.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException
{
    public EventNotFoundException(Long id)
    {
        super("Event with id " + id + " not found");
    }
}
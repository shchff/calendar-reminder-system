package ru.grigorii.calendar_reminder_system.service.exception;

public class CalendarAccessDeniedException extends RuntimeException
{
    public CalendarAccessDeniedException()
    {
        super("Access denied to calendar");
    }
}

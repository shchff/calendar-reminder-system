package ru.grigorii.calendar_reminder_system.service.exception;

import jakarta.persistence.EntityNotFoundException;

/**
 * Исключение, связанное с отсутствием в БД пользователя с указанным id
 */
public class UserNotFoundException extends EntityNotFoundException
{
    private static final String message = "User with id=%d not found";

    public UserNotFoundException(Long id)
    {
        super(String.format(message, id));
    }
}

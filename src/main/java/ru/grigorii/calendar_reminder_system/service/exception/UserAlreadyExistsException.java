package ru.grigorii.calendar_reminder_system.service.exception;

/**
 * Исключение, возникающее при попытке создать пользователя с указанным id или email
 */
public class UserAlreadyExistsException extends ServiceException
{
    private static final String idCauseMessage = "User with id=%d already exists";
    private static final String emailCauseMessage = "User with email=%s already exists";

    public UserAlreadyExistsException(Long id, Throwable cause)
    {
        super(String.format(idCauseMessage, id), cause);
    }

    public UserAlreadyExistsException(String email)
    {
        super(String.format(emailCauseMessage, email));
    }
}

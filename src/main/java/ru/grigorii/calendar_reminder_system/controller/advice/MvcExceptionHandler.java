package ru.grigorii.calendar_reminder_system.controller.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarAccessDeniedException;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarNotFoundException;
import ru.grigorii.calendar_reminder_system.service.exception.EventNotFoundException;
import ru.grigorii.calendar_reminder_system.service.exception.UserAlreadyExistsException;

/**
 * Advice для обработки ошибок в web
 */
@ControllerAdvice
public class MvcExceptionHandler
{
    /**
     * Обработка ошибки, когда пользователь с указанной почтой существует
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserExists(
            UserAlreadyExistsException ex,
            Model model
    )
    {
        model.addAttribute("registrationError", ex.getMessage());
        return "auth/register";
    }

    /**
     * Обработка ошикби, когда календарь не найден
     */
    @ExceptionHandler(CalendarNotFoundException.class)
    public String handleCalendarNotFound(CalendarNotFoundException ex, Model model)
    {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    /**
     * Обработка ошибки, когда событие не найдено
     */
    @ExceptionHandler(EventNotFoundException.class)
    public String handleEventNotFound(EventNotFoundException ex, Model model)
    {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    /**
     * Обработка ошикби, когда доступ к календарю запрещён
     */
    @ExceptionHandler(CalendarAccessDeniedException.class)
    public String handleAccessDenied(Model model)
    {
        model.addAttribute("error", "Доступ запрещён");
        return "error/403";
    }
}

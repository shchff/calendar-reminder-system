package ru.grigorii.calendar_reminder_system.controller.advice;

import java.time.Instant;

/**
 * DTO ошибки API
 * @param status статус-код
 * @param error ошибка
 * @param message сообщение ошибки
 * @param path путь ошибки
 * @param timestamp время
 */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
)
{
}

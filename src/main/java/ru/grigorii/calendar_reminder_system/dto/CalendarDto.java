package ru.grigorii.calendar_reminder_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.grigorii.calendar_reminder_system.model.Calendar;

import java.time.LocalDateTime;

/**
 * DTO календаря
 * @param id id
 * @param name название
 * @param description описание
 * @param ownerId id владельца (пользователя)
 * @param createdAt время создания
 */
public record CalendarDto(
        Long id,

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        Long ownerId,

        LocalDateTime createdAt
)
{
    public static CalendarDto forCreate(String name, String description, Long ownerId)
    {
        return new CalendarDto(null, name, description, ownerId, null);
    }


    public static CalendarDto fromEntity(Calendar entity)
    {
        return new CalendarDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getOwner().getId(),
                entity.getCreatedAt());
    }
}

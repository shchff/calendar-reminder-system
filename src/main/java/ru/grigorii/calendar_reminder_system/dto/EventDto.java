package ru.grigorii.calendar_reminder_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.grigorii.calendar_reminder_system.model.Event;

import java.time.LocalDateTime;

/**
 * DTO события
 * @param id id
 * @param title название
 * @param description описание
 * @param startTime время начала
 * @param endTime время окончания
 * @param priority приоритет
 * @param done сделано / не сделано
 * @param calendarId id календаря, которому принадлежит события
 * @param createdAt время создания
 * @param recurrence dto повторения
 * @param reminder dto напоминания
 */
public record EventDto(
        Long id,

        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        @Pattern(regexp = "HIGH|MEDIUM|LOW")
        String priority,

        Boolean done,

        @NotNull
        Long calendarId,

        LocalDateTime createdAt,

        EventRecurrenceDto recurrence,

        ReminderDto reminder
)
{
    public static EventDto forCreate(Long calendarId) {
        return new EventDto(
                null,
                "",
                "",
                null,
                null,
                "MEDIUM",
                false,
                calendarId,
                null,
                EventRecurrenceDto.nullDto(),
                ReminderDto.nullDto()
        );
    }

    public static EventDto fromEntity(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getPriority().name(),
                event.getDone(),
                event.getCalendar().getId(),
                event.getCreatedAt(),
                event.getRecurrence() != null
                        ? EventRecurrenceDto.fromEntity(event.getRecurrence())
                        : null,
                event.getReminder() != null
                        ? ReminderDto.fromEntity(event.getReminder())
                        : null
        );
    }
}

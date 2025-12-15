package ru.grigorii.calendar_reminder_system.dto;

import jakarta.validation.constraints.NotNull;
import ru.grigorii.calendar_reminder_system.dto.validation_groups.OnAdmin;
import ru.grigorii.calendar_reminder_system.model.EventRecurrence;
import ru.grigorii.calendar_reminder_system.model.RecurrenceType;

import java.time.LocalDate;

/**
 * DTO повторения события
 * @param id id повторения
 * @param eventId id события
 * @param fromDate дата начала повторений
 * @param untilDate дата окончания повторений
 * @param type тип повторений
 */
public record EventRecurrenceDto(
        Long id,

        @NotNull(groups = OnAdmin.class)
        Long eventId,

        @NotNull
        LocalDate fromDate,

        LocalDate untilDate,

        RecurrenceType type
)
{
    public boolean isEnabled()
    {
        return type != null && type != RecurrenceType.NONE;
    }

    public static EventRecurrenceDto nullDto()
    {
        return new EventRecurrenceDto(null, null, null, null, null);
    }

    public static EventRecurrenceDto forCreate(Long eventId,
                                               LocalDate fromDate,
                                               LocalDate untilDate,
                                               RecurrenceType type)
    {
        return new EventRecurrenceDto(
                null,
                eventId,
                fromDate,
                untilDate,
                type
        );
    }

    public static EventRecurrenceDto fromEntity(EventRecurrence recurrence)
    {
        return new EventRecurrenceDto(
                recurrence.getId(),
                recurrence.getEvent().getId(),
                recurrence.getFromDate(),
                recurrence.getUntilDate(),
                recurrence.getRecurrenceType()
        );
    }
}

package ru.grigorii.calendar_reminder_system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.grigorii.calendar_reminder_system.dto.validation_groups.OnAdmin;
import ru.grigorii.calendar_reminder_system.model.Reminder;

import java.time.LocalDateTime;

/**
 * DTO напоминания о событии
 * @param id id напоминания
 * @param eventId id события
 * @param remindAt во сколько напомнить
 * @param channel канал передачи события
 * @param read флаг прочтения
 * @param eventTitle название события
 */
public record ReminderDto(
        Long id,

        @NotNull(groups = OnAdmin.class)
        Long eventId,

        @NotNull
        @Future
        LocalDateTime remindAt,

        @Pattern(regexp = "EMAIL|PUSH|SMS")
        String channel,

        Boolean read,
        String eventTitle
)
{

    public boolean isEnabled()
    {
        return remindAt != null && channel != null;
    }

    public static ReminderDto forCreate(Long eventId,
                                        LocalDateTime remindAt,
                                        String channel)
    {
        return new ReminderDto(
                null,
                eventId,
                remindAt,
                channel,
                false,
                null
        );
    }

    public static ReminderDto nullDto()
    {
        return new ReminderDto(null, null, null, null, null, null);
    }


    public static ReminderDto fromEntity(Reminder reminder)
    {
        return new ReminderDto(
                reminder.getId(),
                reminder.getEvent().getId(),
                reminder.getRemindAt(),
                reminder.getChannel().name(),
                reminder.getRead(),
                reminder.getEvent().getTitle()
        );
    }
}

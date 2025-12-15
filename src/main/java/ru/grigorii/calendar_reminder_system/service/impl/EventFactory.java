package ru.grigorii.calendar_reminder_system.service.impl;

import org.springframework.stereotype.Component;
import ru.grigorii.calendar_reminder_system.dto.EventDto;
import ru.grigorii.calendar_reminder_system.dto.EventRecurrenceDto;
import ru.grigorii.calendar_reminder_system.dto.ReminderDto;
import ru.grigorii.calendar_reminder_system.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Данному классу делегируются операции над событиями внутри сервиса событий
 */
@Component
public class EventFactory
{

    /**
     * Создание события
     */
    public Event createEvent(EventDto dto, Calendar calendar)
    {

        validate(dto);

        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setStartTime(dto.startTime());
        event.setEndTime(dto.endTime());
        event.setPriority(EventPriority.valueOf(dto.priority()));
        event.setDone(false);
        event.setCalendar(calendar);
        event.setCreatedAt(LocalDateTime.now());

        if (dto.recurrence() != null && dto.recurrence().isEnabled())
        {
            event.setRecurrence(createRecurrence(dto.recurrence(), event));
        }

        if (dto.reminder() != null && dto.reminder().isEnabled())
        {
            event.setReminder(createReminder(dto.reminder(), event));
        }

        return event;
    }

    /**
     * Создание повторения события
     */
    private EventRecurrence createRecurrence(EventRecurrenceDto dto,
                                             Event event)
    {

        EventRecurrence r = new EventRecurrence();
        r.setEvent(event);
        r.setFromDate(dto.fromDate());
        r.setUntilDate(dto.untilDate());
        r.setRecurrenceType(dto.type());

        return r;
    }

    /**
     * Создание напоминания
     */
    private Reminder createReminder(ReminderDto dto, Event event)
    {
        Reminder r = new Reminder();
        r.setEvent(event);
        r.setRemindAt(dto.remindAt());
        r.setChannel(ReminderChannel.valueOf(dto.channel()));
        r.setRead(false);
        return r;
    }

    /**
     * Создание события, используется после того, как было выполнено предшествующее ей
     */
    public Event createNextEvent(Event source,
                                 LocalDateTime nextStart)
    {

        LocalDateTime nextEnd = source.getEndTime() != null
                ? nextStart.plus(
                Duration.between(
                        source.getStartTime(),
                        source.getEndTime()
                ))
                : null;

        Event next = new Event();
        next.setTitle(source.getTitle());
        next.setDescription(source.getDescription());
        next.setStartTime(nextStart);
        next.setEndTime(nextEnd);
        next.setPriority(source.getPriority());
        next.setDone(false);
        next.setCalendar(source.getCalendar());
        next.setCreatedAt(LocalDateTime.now());

        return next;
    }

    /**
     * Копирование повторений
     */
    public EventRecurrence copyRecurrence(EventRecurrence source,
                                          Event target,
                                          LocalDateTime nextStart)
    {

        EventRecurrence r = new EventRecurrence();
        r.setEvent(target);
        r.setFromDate(nextStart.toLocalDate());
        r.setUntilDate(source.getUntilDate());
        r.setRecurrenceType(source.getRecurrenceType());

        return r;
    }

    /**
     * Копирование напоминания
     */
    public Reminder copyReminder(Reminder source,
                                 Event target,
                                 LocalDateTime nextStart,
                                 LocalDateTime prevStart)
    {

        Duration delta = Duration.between(
                prevStart,
                source.getRemindAt()
        );

        Reminder reminder = new Reminder();
        reminder.setEvent(target);
        reminder.setRemindAt(nextStart.plus(delta));
        reminder.setChannel(source.getChannel());
        reminder.setRead(false);

        return reminder;
    }

    /**
     * Валидация (проверка, что время начала не находится после времени окончания)
     */
    private void validate(EventDto dto)
    {
        if (dto.endTime().isBefore(dto.startTime()))
        {
            throw new IllegalArgumentException("endTime < startTime");
        }
    }
}

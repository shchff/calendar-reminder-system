package ru.grigorii.calendar_reminder_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.grigorii.calendar_reminder_system.dto.*;
import ru.grigorii.calendar_reminder_system.model.*;
import ru.grigorii.calendar_reminder_system.service.impl.EventFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventFactoryTest
{

    private EventFactory factory;
    private Calendar calendar;

    @BeforeEach
    void setUp()
    {
        factory = new EventFactory();
        calendar = new Calendar();
    }

    @Test
    void createEvent_shouldCreateSimpleEvent()
    {
        EventDto dto = new EventDto(
                null,
                "Title",
                "Desc",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 11, 0),
                "HIGH",
                null,
                100L,
                null,
                null,
                null
        );

        Event event = factory.createEvent(dto, calendar);

        assertEquals("Title", event.getTitle());
        assertEquals(calendar, event.getCalendar());
        assertFalse(event.getDone());
        assertNull(event.getRecurrence());
        assertNull(event.getReminder());
    }

    @Test
    void createEvent_shouldCreateWithRecurrenceAndReminder()
    {
        EventRecurrenceDto recurrence = new EventRecurrenceDto(
                null,
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                RecurrenceType.DAILY
        );

        ReminderDto reminder = new ReminderDto(
                null,
                null,
                LocalDateTime.of(2025, 1, 1, 9, 30),
                "EMAIL",
                false,
                null
        );

        EventDto dto = new EventDto(
                null,
                "Title",
                null,
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 11, 0),
                "LOW",
                false,
                100L,
                null,
                recurrence,
                reminder
        );

        Event event = factory.createEvent(dto, calendar);

        assertNotNull(event.getRecurrence());
        assertNotNull(event.getReminder());
        assertEquals(event, event.getRecurrence().getEvent());
        assertEquals(event, event.getReminder().getEvent());
    }

    @Test
    void createEvent_shouldThrow_whenEndBeforeStart()
    {
        EventDto dto = new EventDto(
                null,
                "Bad",
                null,
                LocalDateTime.of(2025, 1, 1, 11, 0),
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "LOW",
                null,
                100L,
                null,
                null,
                null
        );

        assertThrows(IllegalArgumentException.class,
                () -> factory.createEvent(dto, calendar));
    }

    @Test
    void createNextEvent_shouldPreserveDuration()
    {
        Event source = new Event();
        source.setTitle("Source");
        source.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        source.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        source.setCalendar(calendar);
        source.setPriority(EventPriority.MEDIUM);

        LocalDateTime nextStart = LocalDateTime.of(2025, 1, 2, 10, 0);

        Event next = factory.createNextEvent(source, nextStart);

        assertEquals(nextStart, next.getStartTime());
        assertEquals(
                LocalDateTime.of(2025, 1, 2, 12, 0),
                next.getEndTime()
        );
        assertEquals(calendar, next.getCalendar());
        assertFalse(next.getDone());
    }

    @Test
    void copyRecurrence_shouldCopyCorrectly()
    {
        Event sourceEvent = new Event();
        Event targetEvent = new Event();

        EventRecurrence source = new EventRecurrence();
        source.setRecurrenceType(RecurrenceType.WEEKLY);
        source.setUntilDate(LocalDate.now().plusDays(10));

        LocalDateTime nextStart = LocalDateTime.of(2025, 1, 5, 10, 0);

        EventRecurrence copied =
                factory.copyRecurrence(source, targetEvent, nextStart);

        assertEquals(targetEvent, copied.getEvent());
        assertEquals(nextStart.toLocalDate(), copied.getFromDate());
        assertEquals(source.getUntilDate(), copied.getUntilDate());
        assertEquals(source.getRecurrenceType(), copied.getRecurrenceType());
    }

    @Test
    void copyReminder_shouldShiftRemindAt()
    {
        Event target = new Event();

        Reminder source = new Reminder();
        source.setChannel(ReminderChannel.EMAIL);
        source.setRemindAt(LocalDateTime.of(2025, 1, 1, 9, 0));

        LocalDateTime prevStart =
                LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime nextStart =
                LocalDateTime.of(2025, 1, 2, 10, 0);

        Reminder copied =
                factory.copyReminder(source, target, nextStart, prevStart);

        assertEquals(
                LocalDateTime.of(2025, 1, 2, 9, 0),
                copied.getRemindAt()
        );
        assertEquals(target, copied.getEvent());
        assertFalse(copied.getRead());
    }
}

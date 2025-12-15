package ru.grigorii.calendar_reminder_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.grigorii.calendar_reminder_system.dto.EventDto;
import ru.grigorii.calendar_reminder_system.model.*;
import ru.grigorii.calendar_reminder_system.repository.CalendarRepository;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarAccessDeniedException;
import ru.grigorii.calendar_reminder_system.service.impl.EventFactory;
import ru.grigorii.calendar_reminder_system.service.impl.EventServiceImplementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest
{

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private EventFactory eventFactory;

    @InjectMocks
    private EventServiceImplementation service;

    private User owner;
    private Calendar calendar;
    private Event event;

    @BeforeEach
    void setUp()
    {
        owner = new User();
        owner.setId(1L);

        calendar = new Calendar();
        calendar.setId(10L);
        calendar.setOwner(owner);

        event = new Event();
        event.setId(100L);
        event.setTitle("Title");
        event.setCalendar(calendar);
        event.setDone(false);
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
    }

    @Test
    void findByCalendar_shouldReturnEvents_forOwner()
    {
        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));
        when(eventRepository.findByCalendarId(10L))
                .thenReturn(List.of(event));

        List<EventDto> result = service.findByCalendar(10L, 1L);

        assertEquals(1, result.size());
    }

    @Test
    void findByCalendar_shouldThrow_whenNotOwner()
    {
        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        assertThrows(CalendarAccessDeniedException.class,
                () -> service.findByCalendar(10L, 999L));
    }

    @Test
    void create_shouldCreateEvent_forOwner()
    {
        EventDto dto = new EventDto(
                null,
                "Title",
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "MEDIUM",
                false,
                10L,
                null,
                null,
                null
        );

        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));
        when(eventFactory.createEvent(dto, calendar))
                .thenReturn(event);
        when(eventRepository.save(event))
                .thenReturn(event);

        EventDto result = service.create(dto, 1L);

        assertEquals("Title", result.title());
        verify(eventRepository).save(event);
    }

    @Test
    void create_shouldThrow_whenNotOwner()
    {
        EventDto dto = new EventDto(
                null, "T", null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "LOW", false, 10L,
                null, null, null
        );

        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        assertThrows(CalendarAccessDeniedException.class,
                () -> service.create(dto, 999L));
    }

    @Test
    void delete_shouldDelete_forOwner()
    {
        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));

        service.delete(100L, 1L);

        verify(eventRepository).delete(event);
    }

    @Test
    void markDone_shouldSetDone_forSimpleEvent()
    {
        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));

        service.markDone(100L, 1L);

        assertTrue(event.getDone());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void markDone_shouldCreateNextEvent_forRecurring()
    {
        EventRecurrence recurrence = new EventRecurrence();
        recurrence.setRecurrenceType(RecurrenceType.DAILY);
        recurrence.setFromDate(LocalDate.now());
        recurrence.setUntilDate(LocalDate.now().plusDays(5));
        event.setRecurrence(recurrence);

        Event nextEvent = new Event();

        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));
        when(eventFactory.createNextEvent(any(), any()))
                .thenReturn(nextEvent);

        service.markDone(100L, 1L);

        assertTrue(event.getDone());
        verify(eventRepository).save(nextEvent);
    }

    @Test
    void markUndone_shouldUnsetDone()
    {
        event.setDone(true);

        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));

        service.markUndone(100L, 1L);

        assertFalse(event.getDone());
    }

    @Test
    void findById_shouldThrow_whenNotFound()
    {
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findById(999L));
    }

    @Test
    void delete_rest_shouldThrow_whenNotFound()
    {
        when(eventRepository.existsById(100L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.delete(100L));
    }
}

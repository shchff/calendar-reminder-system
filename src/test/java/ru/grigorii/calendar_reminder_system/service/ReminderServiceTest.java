package ru.grigorii.calendar_reminder_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.grigorii.calendar_reminder_system.dto.ReminderDto;
import ru.grigorii.calendar_reminder_system.model.*;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.repository.ReminderRepository;
import ru.grigorii.calendar_reminder_system.service.impl.ReminderServiceImplementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest
{

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReminderServiceImplementation service;

    private User owner;
    private Calendar calendar;
    private Event event;
    private Reminder reminder;

    @BeforeEach
    void setUp()
    {
        owner = new User();
        owner.setId(1L);

        calendar = new Calendar();
        calendar.setOwner(owner);

        event = new Event();
        event.setId(100L);
        event.setCalendar(calendar);

        reminder = new Reminder();
        reminder.setId(10L);
        reminder.setEvent(event);
        reminder.setRead(false);
    }

    @Test
    void findActive_shouldReturnActiveReminders()
    {
        when(reminderRepository.findActiveUnread(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(reminder));

        List<ReminderDto> result = service.findActive(1L);

        assertEquals(1, result.size());
    }

    @Test
    void countActive_shouldReturnCount()
    {
        when(reminderRepository.countActiveUnread(eq(1L), any(LocalDateTime.class)))
                .thenReturn(2L);

        long count = service.countActive(1L);

        assertEquals(2L, count);
    }

    @Test
    void markAsRead_shouldMarkRead_whenOwnerMatches()
    {
        when(reminderRepository.findById(10L))
                .thenReturn(Optional.of(reminder));

        service.markAsRead(10L, 1L);

        assertTrue(reminder.getRead());
    }

    @Test
    void markAsRead_shouldThrow_whenNotOwner()
    {
        when(reminderRepository.findById(10L))
                .thenReturn(Optional.of(reminder));

        assertThrows(AccessDeniedException.class,
                () -> service.markAsRead(10L, 999L));
    }

    @Test
    void findById_shouldThrow_whenNotFound()
    {
        when(reminderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findById(99L));
    }

    @Test
    void findByEventId_shouldReturnReminder()
    {
        when(reminderRepository.findByEventId(100L))
                .thenReturn(Optional.of(reminder));

        ReminderDto dto = service.findByEventId(100L);

        assertNotNull(dto);
    }

    @Test
    void create_shouldCreateReminder()
    {
        ReminderDto dto = new ReminderDto(
                null,
                100L,
                LocalDateTime.now(),
                "EMAIL",
                false,
                ""
        );

        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));
        when(reminderRepository.save(any(Reminder.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ReminderDto result = service.create(dto);

        assertEquals("EMAIL", result.channel());
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void update_shouldUpdateReminder()
    {
        ReminderDto dto = new ReminderDto(
                null,
                100L,
                LocalDateTime.now(),
                "SMS",
                true,
                ""
        );

        when(reminderRepository.findById(10L))
                .thenReturn(Optional.of(reminder));
        when(eventRepository.findById(100L))
                .thenReturn(Optional.of(event));

        ReminderDto result = service.update(10L, dto);

        assertEquals("SMS", result.channel());
        assertTrue(reminder.getRead());
    }

    @Test
    void delete_shouldDelete()
    {
        when(reminderRepository.existsById(10L))
                .thenReturn(true);

        service.delete(10L);

        verify(reminderRepository).deleteById(10L);
    }

    @Test
    void delete_shouldThrow_whenNotExists()
    {
        when(reminderRepository.existsById(10L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.delete(10L));
    }
}

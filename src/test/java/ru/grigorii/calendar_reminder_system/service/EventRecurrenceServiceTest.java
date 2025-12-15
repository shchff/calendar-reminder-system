package ru.grigorii.calendar_reminder_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.grigorii.calendar_reminder_system.dto.EventRecurrenceDto;
import ru.grigorii.calendar_reminder_system.model.Event;
import ru.grigorii.calendar_reminder_system.model.EventRecurrence;
import ru.grigorii.calendar_reminder_system.model.RecurrenceType;
import ru.grigorii.calendar_reminder_system.repository.EventRecurrenceRepository;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.service.impl.EventRecurrenceServiceImplementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRecurrenceServiceTest
{

    @Mock
    private EventRecurrenceRepository recurrenceRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventRecurrenceServiceImplementation service;

    private Event event;
    private EventRecurrence recurrence;

    @BeforeEach
    void setUp()
    {
        event = new Event();
        event.setId(10L);

        recurrence = new EventRecurrence();
        recurrence.setId(100L);
        recurrence.setEvent(event);
        recurrence.setRecurrenceType(RecurrenceType.DAILY);
        recurrence.setFromDate(LocalDate.now());
    }

    @Test
    void findAll_shouldReturnList()
    {
        when(recurrenceRepository.findAll())
                .thenReturn(List.of(recurrence));

        List<EventRecurrenceDto> result = service.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById_shouldReturnDto()
    {
        when(recurrenceRepository.findById(100L))
                .thenReturn(Optional.of(recurrence));

        EventRecurrenceDto dto = service.findById(100L);

        assertEquals(RecurrenceType.DAILY, dto.type());
    }

    @Test
    void findById_shouldThrow_whenNotFound()
    {
        when(recurrenceRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findById(999L));
    }

    @Test
    void findByEventId_shouldReturnRecurrence()
    {
        when(eventRepository.existsById(10L))
                .thenReturn(true);
        when(recurrenceRepository.findByEventId(10L))
                .thenReturn(Optional.of(recurrence));

        EventRecurrenceDto dto = service.findByEventId(10L);

        assertEquals(RecurrenceType.DAILY, dto.type());
    }

    @Test
    void findByEventId_shouldThrow_whenEventNotExists()
    {
        when(eventRepository.existsById(10L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.findByEventId(10L));
    }

    @Test
    void findByEventId_shouldThrow_whenRecurrenceNotExists()
    {
        when(eventRepository.existsById(10L))
                .thenReturn(true);
        when(recurrenceRepository.findByEventId(10L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findByEventId(10L));
    }

    @Test
    void create_shouldCreateRecurrence()
    {
        EventRecurrenceDto dto = EventRecurrenceDto.forCreate(
                10L,
                LocalDate.now(),
                null,
                RecurrenceType.WEEKLY
        );

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));
        when(recurrenceRepository.save(any(EventRecurrence.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EventRecurrenceDto result = service.create(dto);

        assertEquals(RecurrenceType.WEEKLY, result.type());
        verify(recurrenceRepository).save(any(EventRecurrence.class));
    }

    @Test
    void update_shouldUpdateRecurrence()
    {
        EventRecurrenceDto dto = EventRecurrenceDto.forCreate(
                10L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                RecurrenceType.MONTHLY
        );

        when(recurrenceRepository.findById(100L))
                .thenReturn(Optional.of(recurrence));
        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        EventRecurrenceDto result = service.update(100L, dto);

        assertEquals(RecurrenceType.MONTHLY, result.type());
    }

    @Test
    void delete_shouldDelete()
    {
        when(recurrenceRepository.existsById(100L))
                .thenReturn(true);

        service.delete(100L);

        verify(recurrenceRepository).deleteById(100L);
    }

    @Test
    void delete_shouldThrow_whenNotExists()
    {
        when(recurrenceRepository.existsById(100L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.delete(100L));
    }
}

package ru.grigorii.calendar_reminder_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.model.Calendar;
import ru.grigorii.calendar_reminder_system.model.User;
import ru.grigorii.calendar_reminder_system.repository.CalendarRepository;
import ru.grigorii.calendar_reminder_system.repository.UserRepository;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarAccessDeniedException;
import ru.grigorii.calendar_reminder_system.service.impl.CalendarServiceImplementation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest
{

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CalendarServiceImplementation service;

    private User owner;
    private Calendar calendar;

    @BeforeEach
    void setUp()
    {
        owner = new User();
        owner.setId(1L);

        calendar = new Calendar();
        calendar.setId(10L);
        calendar.setName("Old name");
        calendar.setOwner(owner);
    }

    @Test
    void findByOwner_shouldReturnCalendars()
    {
        when(calendarRepository.findByOwnerId(1L))
                .thenReturn(List.of(calendar));

        List<CalendarDto> result = service.findByOwner(1L);

        assertEquals(1, result.size());
        assertEquals("Old name", result.getFirst().name());
    }

    @Test
    void createCalendar_shouldCreate()
    {
        CalendarDto dto = new CalendarDto(null, "Test", "Desc", 1L, null);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(calendarRepository.save(any(Calendar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CalendarDto result = service.createCalendar(dto);

        assertEquals("Test", result.name());
        verify(calendarRepository).save(any(Calendar.class));
    }

    @Test
    void updateForOwner_shouldUpdate_whenOwnerMatches()
    {
        CalendarDto dto = new CalendarDto(null, "New", "New desc", 1L, null);

        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        service.updateForOwner(10L, dto, 1L);

        assertEquals("New", calendar.getName());
        assertEquals("New desc", calendar.getDescription());
    }

    @Test
    void updateForOwner_shouldThrow_whenNotOwner()
    {
        CalendarDto dto = new CalendarDto(null, "New", "Desc", 1L, null);

        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        assertThrows(CalendarAccessDeniedException.class,
                () -> service.updateForOwner(10L, dto, 999L));
    }

    @Test
    void delete_shouldDelete_whenOwnerMatches()
    {
        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        service.deleteById(10L, 1L);

        verify(calendarRepository).delete(calendar);
    }

    @Test
    void delete_shouldThrow_whenNotOwner()
    {
        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        assertThrows(CalendarAccessDeniedException.class,
                () -> service.deleteById(10L, 999L));
    }

    @Test
    void findByIdForOwner_shouldReturnCalendar()
    {
        when(calendarRepository.findById(10L))
                .thenReturn(Optional.of(calendar));

        CalendarDto dto = service.findByIdForOwner(10L, 1L);

        assertEquals("Old name", dto.name());
    }

    @Test
    void findById_shouldThrow_whenNotFound()
    {
        when(calendarRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findById(99L));
    }

    @Test
    void findByOwnerId_shouldThrow_whenUserNotExists()
    {
        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.findByOwnerId(1L));
    }

    @Test
    void create_shouldCreateCalendar()
    {
        CalendarDto dto = new CalendarDto(null, "Name", "Desc", 1L, null);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(calendarRepository.save(any(Calendar.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CalendarDto result = service.create(dto);

        assertEquals("Name", result.name());
    }
}

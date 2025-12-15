package ru.grigorii.calendar_reminder_system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.grigorii.calendar_reminder_system.dto.EventRecurrenceDto;
import ru.grigorii.calendar_reminder_system.model.EventRecurrence;
import ru.grigorii.calendar_reminder_system.repository.EventRecurrenceRepository;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.service.rest.EventRecurrenceRestService;

import java.util.List;

@Service
@Transactional
public class EventRecurrenceServiceImplementation implements EventRecurrenceRestService
{
    private final EventRecurrenceRepository recurrenceRepository;
    private final EventRepository eventRepository;

    public EventRecurrenceServiceImplementation(
            EventRecurrenceRepository recurrenceRepository,
            EventRepository eventRepository
    )
    {
        this.recurrenceRepository = recurrenceRepository;
        this.eventRepository = eventRepository;
    }

    public List<EventRecurrenceDto> findAll()
    {
        return recurrenceRepository.findAll()
                .stream()
                .map(EventRecurrenceDto::fromEntity)
                .toList();
    }

    public EventRecurrenceDto findById(Long id)
    {
        EventRecurrence recurrence = recurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EventRecurrence not found"));
        return EventRecurrenceDto.fromEntity(recurrence);
    }

    @Override
    public EventRecurrenceDto findByEventId(Long eventId)
    {
        if (!eventRepository.existsById(eventId))
        {
            throw new EntityNotFoundException("Event not found");
        }

        EventRecurrence recurrence = recurrenceRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("EventRecurrence not found"));

        return EventRecurrenceDto.fromEntity(recurrence);
    }

    public EventRecurrenceDto create(EventRecurrenceDto dto)
    {
        EventRecurrence recurrence = new EventRecurrence();
        applyDto(recurrence, dto);
        return EventRecurrenceDto.fromEntity(
                recurrenceRepository.save(recurrence)
        );
    }

    /**
     * Изменяет повторение
     * @param id id повторения
     * @param dto
     * @return обновлённое повторение
     */
    public EventRecurrenceDto update(Long id, EventRecurrenceDto dto)
    {
        EventRecurrence recurrence = recurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EventRecurrence not found"));
        applyDto(recurrence, dto);
        return EventRecurrenceDto.fromEntity(recurrence);
    }

    /**
     * Удалить повторение
     * @param id id повторения
     */
    public void delete(Long id)
    {
        if (!recurrenceRepository.existsById(id))
        {
            throw new EntityNotFoundException("EventRecurrence not found");
        }
        recurrenceRepository.deleteById(id);
    }

    /**
     * Маппер
     */
    private void applyDto(EventRecurrence recurrence, EventRecurrenceDto dto)
    {
        recurrence.setRecurrenceType(dto.type());
        recurrence.setFromDate(dto.fromDate());
        recurrence.setUntilDate(dto.untilDate());
        recurrence.setEvent(
                eventRepository.findById(dto.eventId())
                        .orElseThrow(() -> new EntityNotFoundException("Event not found"))
        );
    }
}

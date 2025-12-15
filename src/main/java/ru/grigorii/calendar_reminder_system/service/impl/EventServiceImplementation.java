package ru.grigorii.calendar_reminder_system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.grigorii.calendar_reminder_system.dto.EventDto;
import ru.grigorii.calendar_reminder_system.model.*;
import ru.grigorii.calendar_reminder_system.repository.CalendarRepository;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarAccessDeniedException;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarNotFoundException;
import ru.grigorii.calendar_reminder_system.service.exception.EventNotFoundException;
import ru.grigorii.calendar_reminder_system.service.mvc.EventMvcService;
import ru.grigorii.calendar_reminder_system.service.rest.EventRestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventServiceImplementation
        implements EventMvcService, EventRestService
{

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final EventFactory eventFactory;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository,
                                      CalendarRepository calendarRepository,
                                      EventFactory eventFactory)
    {
        this.eventRepository = eventRepository;
        this.calendarRepository = calendarRepository;
        this.eventFactory = eventFactory;
    }

    /**
     * WEB: возвращает все события календаря
     *
     * @param calendarId id календаря
     * @param ownerId    id владельца
     * @return найденные события
     */
    @Override
    public List<EventDto> findByCalendar(Long calendarId, Long ownerId)
    {
        Calendar calendar = getCalendar(calendarId);
        checkOwner(calendar, ownerId);

        return eventRepository.findByCalendarId(calendarId).stream()
                .map(EventDto::fromEntity)
                .toList();
    }

    /**
     * WEB: создание события
     *
     * @param dto     событие для создания
     * @param ownerId id владельца
     * @return созданное событие
     */
    @Override
    public EventDto create(EventDto dto, Long ownerId)
    {
        Calendar calendar = getCalendar(dto.calendarId());
        checkOwner(calendar, ownerId);

        Event event = eventFactory.createEvent(dto, calendar);
        return EventDto.fromEntity(eventRepository.save(event));
    }

    /**
     * WEB: удаление события
     *
     * @param eventId id события
     * @param ownerId id владельца
     */
    @Override
    public void delete(Long eventId, Long ownerId)
    {
        Event event = getEvent(eventId);
        checkOwner(event.getCalendar(), ownerId);
        eventRepository.delete(event);
    }

    /**
     * WEB: помечивание события как выполненное
     *
     * @param eventId id события
     * @param ownerId id владельца
     */
    @Override
    public void markDone(Long eventId, Long ownerId)
    {
        Event event = getEvent(eventId);
        checkOwner(event.getCalendar(), ownerId);

        if (Boolean.TRUE.equals(event.getDone()))
        {
            return;
        }

        event.setDone(true);

        EventRecurrence recurrence = event.getRecurrence();
        if (recurrence == null)
        {
            return;
        }

        LocalDateTime nextStart = calculateNextOccurrence(
                event.getStartTime(),
                recurrence.getRecurrenceType()
        );

        if (recurrence.getUntilDate() != null &&
                nextStart.toLocalDate().isAfter(recurrence.getUntilDate()))
        {
            return;
        }

        Event nextEvent = eventFactory.createNextEvent(event, nextStart);
        eventRepository.save(nextEvent);

        nextEvent.setRecurrence(
                eventFactory.copyRecurrence(
                        recurrence,
                        nextEvent,
                        nextStart
                )
        );

        if (event.getReminder() != null)
        {
            nextEvent.setReminder(
                    eventFactory.copyReminder(
                            event.getReminder(),
                            nextEvent,
                            nextStart,
                            event.getStartTime()
                    )
            );
        }
    }


    /**
     * WEB: помечает событие как невыполненное
     *
     * @param eventId id события
     * @param ownerId id владельца
     */
    @Override
    public void markUndone(Long eventId, Long ownerId)
    {
        Event event = getEvent(eventId);
        checkOwner(event.getCalendar(), ownerId);

        if (!Boolean.TRUE.equals(event.getDone()))
        {
            return;
        }

        event.setDone(false);
    }

    /**
     * REST API: возвращает все события
     * @return список событий
     */
    @Override
    public List<EventDto> findAll()
    {
        return eventRepository.findAll()
                .stream()
                .map(EventDto::fromEntity)
                .toList();
    }

    @Override
    public List<EventDto> findByCalendar(Long calendarId)
    {
        return eventRepository.findByCalendarId(calendarId).stream()
                .map(EventDto::fromEntity)
                .toList();
    }

    @Override
    public EventDto findById(Long id)
    {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        return EventDto.fromEntity(event);
    }

    @Override
    public EventDto create(EventDto dto)
    {
        Event event = new Event();
        applyDto(event, dto);
        return EventDto.fromEntity(eventRepository.save(event));
    }

    @Override
    public EventDto update(Long id, EventDto dto)
    {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        applyDto(event, dto);
        return EventDto.fromEntity(event);
    }

    @Override
    public void delete(Long id)
    {
        if (!eventRepository.existsById(id))
        {
            throw new EntityNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    private Event getEvent(Long id)
    {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    private Calendar getCalendar(Long id)
    {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new CalendarNotFoundException(id));
    }

    /**
     * Проверка владельца календаря
     */
    private void checkOwner(Calendar calendar, Long ownerId)
    {
        if (!calendar.getOwner().getId().equals(ownerId))
        {
            throw new CalendarAccessDeniedException();
        }
    }

    /**
     * Вычисление времени следующего повторения события
     *
     * @param current текущее время
     * @param type    тип повторения
     * @return вычисленное время
     */
    private LocalDateTime calculateNextOccurrence(LocalDateTime current,
                                                  RecurrenceType type)
    {
        return switch (type)
        {
            case NONE -> null;
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }

    private void applyDto(Event event, EventDto dto)
    {
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setStartTime(dto.startTime());
        event.setEndTime(dto.endTime());
        event.setDone(dto.done() != null ? dto.done() : false);
        event.setPriority(EventPriority.valueOf(dto.priority()));
        event.setCalendar(
                calendarRepository.findById(dto.calendarId())
                        .orElseThrow(() -> new EntityNotFoundException("Calendar not found"))
        );
    }
}

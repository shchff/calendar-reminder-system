package ru.grigorii.calendar_reminder_system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.grigorii.calendar_reminder_system.dto.ReminderDto;
import ru.grigorii.calendar_reminder_system.model.Reminder;
import ru.grigorii.calendar_reminder_system.model.ReminderChannel;
import ru.grigorii.calendar_reminder_system.repository.EventRepository;
import ru.grigorii.calendar_reminder_system.repository.ReminderRepository;
import ru.grigorii.calendar_reminder_system.service.mvc.ReminderMvcService;
import ru.grigorii.calendar_reminder_system.service.rest.ReminderRestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReminderServiceImplementation implements ReminderMvcService, ReminderRestService
{
    private final ReminderRepository reminderRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ReminderServiceImplementation(ReminderRepository reminderRepository, EventRepository eventRepository)
    {
        this.reminderRepository = reminderRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Нахождение непрочитанных напоминаний пользователя
     *
     * @param userId id пользователя
     * @return список напоминаний
     */
    @Override
    public List<ReminderDto> findActive(Long userId)
    {
        return reminderRepository
                .findActiveUnread(userId, LocalDateTime.now())
                .stream()
                .map(ReminderDto::fromEntity)
                .toList();
    }

    /**
     * Количество непрочитанных напоминаний пользователя
     *
     * @param userId id пользователя
     * @return количество напоминаний
     */
    @Override
    public long countActive(Long userId)
    {
        return reminderRepository
                .countActiveUnread(userId, LocalDateTime.now());
    }

    /**
     * Помечивание напоминания как прочитанное
     * @param reminderId id напоминания
     * @param userId id пользователя
     */
    @Override
    public void markAsRead(Long reminderId, Long userId)
    {

        Reminder r = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));

        Long ownerId = r.getEvent()
                .getCalendar()
                .getOwner()
                .getId();

        if (!ownerId.equals(userId))
        {
            throw new AccessDeniedException("Not your reminder");
        }

        r.setRead(true);
    }

    public List<ReminderDto> findAll()
    {
        return reminderRepository.findAll()
                .stream()
                .map(ReminderDto::fromEntity)
                .toList();
    }

    /**
     * REST API: Возвращает напоминание по id
     *
     * @param id id напоминание
     * @return найденное напоминание
     */
    public ReminderDto findById(Long id)
    {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));
        return ReminderDto.fromEntity(reminder);
    }

    /**
     * REST API: возвращает напоминание по id события
     *
     * @param eventId id события
     * @return найденное напоминание
     */
    @Override
    public ReminderDto findByEventId(Long eventId)
    {
        Reminder reminder = reminderRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));

        return ReminderDto.fromEntity(reminder);
    }

    /**
     * REST API: создаёт напоминание
     *
     * @param dto напоминание для создания
     * @return созданное напоминание
     */
    public ReminderDto create(ReminderDto dto)
    {
        Reminder reminder = new Reminder();
        applyDto(reminder, dto);
        return ReminderDto.fromEntity(
                reminderRepository.save(reminder)
        );
    }

    /**
     * REST API: изменяет напоминание
     *
     * @param id  id напоминания
     * @param dto содержимое обновления
     * @return обновлённое напоминание
     */
    public ReminderDto update(Long id, ReminderDto dto)
    {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found"));
        applyDto(reminder, dto);
        return ReminderDto.fromEntity(reminder);
    }

    /**
     * REST API: удаляет напоминание
     *
     * @param id id напоминания
     */
    public void delete(Long id)
    {
        if (!reminderRepository.existsById(id))
        {
            throw new EntityNotFoundException("Reminder not found");
        }
        reminderRepository.deleteById(id);
    }

    /**
     * Маппер
     */
    private void applyDto(Reminder reminder, ReminderDto dto)
    {
        reminder.setRemindAt(dto.remindAt());
        reminder.setChannel(ReminderChannel.valueOf(dto.channel()));
        reminder.setRead(dto.read());
        reminder.setEvent(
                eventRepository.findById(dto.eventId())
                        .orElseThrow(() -> new EntityNotFoundException("Event not found"))
        );
    }
}

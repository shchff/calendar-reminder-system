package ru.grigorii.calendar_reminder_system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.model.Calendar;
import ru.grigorii.calendar_reminder_system.model.User;
import ru.grigorii.calendar_reminder_system.repository.CalendarRepository;
import ru.grigorii.calendar_reminder_system.repository.UserRepository;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarAccessDeniedException;
import ru.grigorii.calendar_reminder_system.service.exception.CalendarNotFoundException;
import ru.grigorii.calendar_reminder_system.service.mvc.CalendarMvcService;
import ru.grigorii.calendar_reminder_system.service.rest.CalendarRestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CalendarServiceImplementation
        implements CalendarMvcService, CalendarRestService
{

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    @Autowired
    public CalendarServiceImplementation(CalendarRepository calendarRepository, UserRepository userRepository)
    {
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
    }

    /**
     * WEB: Найти календари пользователя
     * @param ownerId id пользователя
     * @return список найденных календарей
     */
    @Override
    public List<CalendarDto> findByOwner(Long ownerId)
    {
        return calendarRepository.findByOwnerId(ownerId).stream()
                .map(CalendarDto::fromEntity)
                .toList();
    }

    /**
     * WEB: создание календаря
     * @param dto информация для создания
     * @return созданный календарь
     */
    @Override
    public CalendarDto createCalendar(CalendarDto dto)
    {
        User owner = userRepository.findById(dto.ownerId())
                .orElseThrow();

        Calendar calendar = new Calendar();
        calendar.setName(dto.name());
        calendar.setDescription(dto.description());
        calendar.setOwner(owner);
        calendar.setCreatedAt(LocalDateTime.now());

        return CalendarDto.fromEntity(calendarRepository.save(calendar));
    }

    /**
     * WEB: обновить календаь
     * @param calendarId id календаря
     * @param dto ифнормация для изменения
     * @param ownerId id владельца
     */
    @Override
    public void updateForOwner(Long calendarId,
                               CalendarDto dto,
                               Long ownerId)
    {

        Calendar calendar = getCalendar(calendarId);

        if (!calendar.getOwner().getId().equals(ownerId))
        {
            throw new CalendarAccessDeniedException();
        }

        calendar.setName(dto.name());
        calendar.setDescription(dto.description());
    }

    /**
     * WEB: Удаляет календарь
     * @param calendarId id календаря
     * @param ownerId id владельца
     */
    @Override
    public void deleteById(Long calendarId, Long ownerId)
    {
        Calendar calendar = getCalendar(calendarId);

        if (!calendar.getOwner().getId().equals(ownerId))
        {
            throw new CalendarAccessDeniedException();
        }

        calendarRepository.deleteById(calendarId);
    }

    /**
     * WEB: Найти календарь по id
     * @param calendarId id календаря
     * @param ownerId id владельца
     * @return найденный календарь
     */
    @Override
    public CalendarDto findByIdForOwner(Long calendarId, Long ownerId)
    {
        Calendar calendar = getCalendar(calendarId);

        if (!calendar.getOwner().getId().equals(ownerId))
        {
            throw new CalendarAccessDeniedException();
        }

        return CalendarDto.fromEntity(calendar);
    }

    /**
     * REST API: возвращает все календари
     * @return список календарей
     */
    @Override
    public List<CalendarDto> findAll()
    {
        return calendarRepository.findAll()
                .stream()
                .map(CalendarDto::fromEntity)
                .toList();
    }

    /**
     * REST API: находит календарь по id
     * @param id id календаря
     * @return найденный календарь
     */
    @Override
    public CalendarDto findById(Long id)
    {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar not found"));
        return CalendarDto.fromEntity(calendar);
    }

    /**
     * REST API: Найти календари по id владельца
     * @param ownerId id владельца
     * @return список найденных календарей
     */
    @Override
    public List<CalendarDto> findByOwnerId(Long ownerId)
    {
        if (!userRepository.existsById(ownerId))
        {
            throw new EntityNotFoundException("User not found");
        }

        return calendarRepository.findByOwnerId(ownerId).stream()
                .map(CalendarDto::fromEntity)
                .toList();
    }

    /**
     * REST API: создать календарь
     * @param dto информация для создания
     * @return созданный календарь
     */
    @Override
    public CalendarDto create(CalendarDto dto)
    {
        Calendar calendar = new Calendar();
        applyDto(calendar, dto);
        calendar.setCreatedAt(LocalDateTime.now());
        return CalendarDto.fromEntity(calendarRepository.save(calendar));
    }

    /**
     * REST API: обновление информации о календаре
     * @param id id календаря
     * @param dto обновлённая информация
     * @return обновлённый календарь
     */
    @Override
    public CalendarDto update(Long id, CalendarDto dto)
    {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar not found"));
        applyDto(calendar, dto);
        return CalendarDto.fromEntity(calendar);
    }

    /**
     * REST API: удаляет календарь
     * @param id id календаря
     */
    public void delete(Long id)
    {
        calendarRepository.deleteById(id);
    }

    /**
     * Маппер
     */
    private void applyDto(Calendar calendar, CalendarDto dto)
    {
        calendar.setName(dto.name());
        calendar.setDescription(dto.description());
        calendar.setOwner(
                userRepository.findById(dto.ownerId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }

    /**
     * Возвращает календарь по его id
     * @param id id календаря
     * @return календарь
     */
    private Calendar getCalendar(Long id)
    {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new CalendarNotFoundException(id));
    }
}

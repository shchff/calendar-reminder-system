package ru.grigorii.calendar_reminder_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.grigorii.calendar_reminder_system.model.Calendar;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long>
{
    List<Calendar> findByOwnerId(Long ownerId);
}

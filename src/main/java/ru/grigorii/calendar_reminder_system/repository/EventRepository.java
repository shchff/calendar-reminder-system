package ru.grigorii.calendar_reminder_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.grigorii.calendar_reminder_system.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>
{
    List<Event> findByCalendarId(Long calendarId);
}

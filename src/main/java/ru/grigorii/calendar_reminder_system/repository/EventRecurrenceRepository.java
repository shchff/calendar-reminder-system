package ru.grigorii.calendar_reminder_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.grigorii.calendar_reminder_system.model.EventRecurrence;

import java.util.Optional;

@Repository
public interface EventRecurrenceRepository extends JpaRepository<EventRecurrence, Long>
{
    Optional<EventRecurrence> findByEventId(Long eventId);
}

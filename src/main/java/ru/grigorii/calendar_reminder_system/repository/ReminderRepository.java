package ru.grigorii.calendar_reminder_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.grigorii.calendar_reminder_system.model.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long>
{
    @Query("""
                select r
                from Reminder r
                join r.event e
                join e.calendar c
                where c.owner.id = :userId
                  and r.remindAt <= :now
                  and r.read = false
                order by r.remindAt
            """)
    List<Reminder> findActiveUnread(Long userId, LocalDateTime now);

    @Query("""
                select count(r)
                from Reminder r
                join r.event e
                join e.calendar c
                where c.owner.id = :userId
                  and r.remindAt <= :now
                  and r.read = false
            """)
    long countActiveUnread(Long userId, LocalDateTime now);

    Optional<Reminder> findByEventId(Long eventId);
}
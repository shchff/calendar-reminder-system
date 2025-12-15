package ru.grigorii.calendar_reminder_system.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.grigorii.calendar_reminder_system.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReminderRepositoryTest
{

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void findActiveUnread_shouldReturnOnlyActiveUnreadOrdered()
    {
        User owner = userRepository.save(user("owner@test.com"));
        User other = userRepository.save(user("other@test.com"));

        Calendar calendar = calendarRepository.save(calendar(owner));
        Calendar otherCalendar = calendarRepository.save(calendar(other));

        LocalDateTime now = LocalDateTime.now();

        Event e1 = eventRepository.save(event(calendar));
        Event e2 = eventRepository.save(event(calendar));
        Event futureEvent = eventRepository.save(event(calendar));
        Event readEvent = eventRepository.save(event(calendar));
        Event otherEvent = eventRepository.save(event(otherCalendar));

        Reminder r1 = reminderRepository.save(
                reminder(e1, now.minusMinutes(10), false)
        );
        Reminder r2 = reminderRepository.save(
                reminder(e2, now.minusMinutes(5), false)
        );
        reminderRepository.save(
                reminder(futureEvent, now.plusMinutes(5), false)
        );
        reminderRepository.save(
                reminder(readEvent, now.minusMinutes(1), true)
        );
        reminderRepository.save(
                reminder(otherEvent, now.minusMinutes(1), false)
        );

        List<Reminder> result =
                reminderRepository.findActiveUnread(owner.getId(), now);

        assertEquals(2, result.size());
        assertEquals(r1.getId(), result.get(0).getId());
        assertEquals(r2.getId(), result.get(1).getId());
    }

    @Test
    void countActiveUnread_shouldReturnCorrectCount()
    {
        User owner = userRepository.save(user("owner@test.com"));
        Calendar calendar = calendarRepository.save(calendar(owner));

        LocalDateTime now = LocalDateTime.now();

        Event e1 = eventRepository.save(event(calendar));
        Event e2 = eventRepository.save(event(calendar));
        Event future = eventRepository.save(event(calendar));
        Event read = eventRepository.save(event(calendar));

        reminderRepository.save(reminder(e1, now.minusMinutes(5), false));
        reminderRepository.save(reminder(e2, now.minusMinutes(1), false));
        reminderRepository.save(reminder(future, now.plusMinutes(5), false));
        reminderRepository.save(reminder(read, now.minusMinutes(1), true));

        long count = reminderRepository.countActiveUnread(owner.getId(), now);

        assertEquals(2, count);
    }

    @Test
    void findByEventId_shouldReturnReminder()
    {
        User owner = userRepository.save(user("owner@test.com"));
        Calendar calendar = calendarRepository.save(calendar(owner));
        Event event = eventRepository.save(event(calendar));

        Reminder reminder = reminderRepository.save(
                reminder(event, LocalDateTime.now(), false)
        );

        Optional<Reminder> result =
                reminderRepository.findByEventId(event.getId());

        assertTrue(result.isPresent());
        assertEquals(reminder.getId(), result.get().getId());
    }

    @Test
    void findByEventId_shouldReturnEmpty()
    {
        Optional<Reminder> result =
                reminderRepository.findByEventId(999L);

        assertTrue(result.isEmpty());
    }

    private User user(String email)
    {
        User u = new User();
        u.setEmail(email);
        u.setName("Test");
        u.setSurname("User");
        u.setRole(Role.USER);
        u.setPasswordHash("hash");
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }

    private Calendar calendar(User owner)
    {
        Calendar c = new Calendar();
        c.setName("Calendar");
        c.setOwner(owner);
        return c;
    }

    private Event event(Calendar calendar)
    {
        Event e = new Event();
        e.setTitle("Event");
        e.setStartTime(LocalDateTime.now());
        e.setEndTime(LocalDateTime.now().plusHours(1));
        e.setPriority(EventPriority.MEDIUM);
        e.setDone(false);
        e.setCalendar(calendar);
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    private Reminder reminder(Event event,
                              LocalDateTime remindAt,
                              boolean read)
    {
        Reminder r = new Reminder();
        r.setEvent(event);
        r.setRemindAt(remindAt);
        r.setRead(read);
        r.setChannel(ReminderChannel.EMAIL);
        return r;
    }
}


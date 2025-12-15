package ru.grigorii.calendar_reminder_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Сущность напоминание
 */
@Entity
@Table(name = "reminder")
public class Reminder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Событие, о котором нужно напомнить
     */
    @OneToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    /**
     * Когда отправить напоминание
     */
    @Column(name = "remind_at", nullable = false)
    private LocalDateTime remindAt;

    /**
     * Отправлено ли напоминание
     */
    @Column(nullable = false)
    private Boolean read = false;

    /**
     * Канал отправки напоминания
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReminderChannel channel = ReminderChannel.PUSH;

    /**
     * Фабричный метод для создания напоминания с необходимыми полями
     */
    public static Reminder requiredFields(Event event, LocalDateTime remindAt)
    {
        Reminder reminder = new Reminder();
        reminder.setEvent(event);
        reminder.setRemindAt(remindAt);

        return reminder;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }

    public LocalDateTime getRemindAt()
    {
        return remindAt;
    }

    public void setRemindAt(LocalDateTime remindAt)
    {
        this.remindAt = remindAt;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }

    public ReminderChannel getChannel()
    {
        return channel;
    }

    public void setChannel(ReminderChannel channel)
    {
        this.channel = channel;
    }
}

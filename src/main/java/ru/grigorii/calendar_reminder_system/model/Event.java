package ru.grigorii.calendar_reminder_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Сущность событие
 */
@Entity
@Table(name = "events")
public class Event
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название
     */
    @Column(length = 200, nullable = false)
    private String title;

    /**
     * Описание
     */
    private String description;

    /**
     * Время начала <br/>
     * Трактовка 1: если событие может произойти в любой момент дня, то хранится время с точностью до дня <br/>
     * Трактовка 2: если событие должно произойти в определённый момент, то хранится время с точностью до минут
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Время окончания
     */
    @Column(name = "until_time")
    private LocalDateTime endTime;

    /**
     * Приоритет события
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EventPriority priority = EventPriority.MEDIUM;

    /**
     * Выполнено ли событие
     */
    @Column(nullable = false)
    private Boolean done = false;

    /**
     * Календарь, в котором находится событие
     */
    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    /**
     * Время создания события
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Правило повторения события
     */
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private EventRecurrence recurrence;

    /**
     * Напоминания события
     */
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reminder reminder;

    /**
     * Фабричный метод для создания события с необходимыми полями
     */
    public static Event requiredFields(String title, LocalDateTime startTime, Calendar calendar, User createdBy)
    {
        Event event = new Event();
        event.setTitle(title);
        event.setStartTime(startTime);
        event.setCalendar(calendar);

        return event;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }

    public EventPriority getPriority()
    {
        return priority;
    }

    public void setPriority(EventPriority priority)
    {
        this.priority = priority;
    }

    public Boolean getDone()
    {
        return done;
    }

    public void setDone(Boolean done)
    {
        this.done = done;
    }

    public Calendar getCalendar()
    {
        return calendar;
    }

    public void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public EventRecurrence getRecurrence()
    {
        return recurrence;
    }

    public void setRecurrence(EventRecurrence recurrence)
    {
        this.recurrence = recurrence;
    }

    public Reminder getReminder()
    {
        return reminder;
    }

    public void setReminder(Reminder reminder)
    {
        this.reminder = reminder;
    }
}

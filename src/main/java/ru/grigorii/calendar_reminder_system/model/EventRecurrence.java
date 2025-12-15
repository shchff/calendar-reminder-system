package ru.grigorii.calendar_reminder_system.model;

import jakarta.persistence.*;

import java.time.LocalDate;
/**
 * Сущность повторение события
 */
@Entity
@Table(name = "event_recurrences")
public class EventRecurrence
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Событие, для которого производятся повторения
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    /**
     * Дата начала повторений
     */
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    /**
     * Дата окончания повторений
     */
    @Column(name = "until_date")
    private LocalDate untilDate;

    /**
     * Правило повторения события, буду реализовывать через RRULE, RFC 5545.
     */
    @Column(name = "type", nullable = false)
    private RecurrenceType recurrenceType;

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

    public LocalDate getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate)
    {
        this.fromDate = fromDate;
    }

    public LocalDate getUntilDate()
    {
        return untilDate;
    }

    public void setUntilDate(LocalDate untilDate)
    {
        this.untilDate = untilDate;
    }

    public RecurrenceType getRecurrenceType()
    {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType)
    {
        this.recurrenceType = recurrenceType;
    }
}

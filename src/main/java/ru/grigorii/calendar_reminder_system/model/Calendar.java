package ru.grigorii.calendar_reminder_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность календарь
 */
@Entity
@Table(name = "calendars")
public class Calendar
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя календаря
     */
    @Column(length = 100, nullable = false)
    private String name;

    /**
     * Описание календаря
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Владелец / создатель календаря
     */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Время создания календаря
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Event> events;

    /**
     * Фабричный метод для создания календаря с необходимыми полями
     */
    public static Calendar requiredFields(String name, User owner)
    {
        Calendar calendar = new Calendar();
        calendar.setName(name);
        calendar.setOwner(owner);

        return calendar;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public User getOwner()
    {
        return owner;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }
}

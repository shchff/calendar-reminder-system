package ru.grigorii.calendar_reminder_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность пользователь
 */
@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя
     */
    @Column(length = 50)
    private String name;

    /**
     * Фамилия
     */
    @Column(length = 50)
    private String surname;

    /**
     * Электронная почта
     */
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    /**
     * Хэш пароля
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Роль пользователя
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    /**
     * Время создания пользователя
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Calendar> calendars;

    /**
     * Фабричный метод для создания пользователя с необходимыми полями
     */
    public static User requiredFields(String email, String passwordHash)
    {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        return user;
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

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}

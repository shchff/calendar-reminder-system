package ru.grigorii.calendar_reminder_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.grigorii.calendar_reminder_system.model.User;

import java.time.LocalDateTime;

/**
 * DTO пользовталея
 * @param id id пользователя
 * @param name имя
 * @param surname фамимлия
 * @param email email пользователя
 * @param password пароль пользователя (в чистом виде при создании, в остальных случаях - хэш)
 * @param role роль пользователя
 * @param createdAt время создания
 */
public record UserDto(
        Long id,

        @NotBlank
        @Size(max = 50)
        String name,

        @NotBlank
        @Size(max = 50)
        String surname,

        @Email
        @NotBlank
        @Size(max = 100)
        String email,

        @Size(min = 6, max = 60)
        String password,

        @Pattern(regexp = "ADMIN|USER")
        String role,

        LocalDateTime createdAt
)
{
    public static UserDto forCreate(String name, String surname, String email, String password, String role)
    {
        return new UserDto(null, name, surname, email, password, role, null);
    }

    public static UserDto forUpdate(Long id, String name, String surname, String email)
    {
        return new UserDto(id, name, surname, email, null, null, null);
    }

    public static UserDto fromEntity(User entity)
    {
        return new UserDto(entity.getId(), entity.getName(), entity.getSurname(),
                entity.getEmail(), null, entity.getRole().name(), entity.getCreatedAt());
    }

    public static UserDto empty()
    {
        return new UserDto(null, "", "", "", "", "USER", null);
    }
}

package ru.grigorii.calendar_reminder_system.service.mvc;

import ru.grigorii.calendar_reminder_system.dto.UserDto;

public interface UserMvcService
{
    UserDto register(UserDto dto);
    void updateProfile(String currentEmail, UserDto dto);
    void deleteAccount(String email, String password);
    UserDto getByEmail(String email);
    void changePassword(String email, String oldPassword, String newPassword);
}

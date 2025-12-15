package ru.grigorii.calendar_reminder_system.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.ReminderMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Advice для счётчика уведомлений
 */
@ControllerAdvice
public class NotificationAdvice
{

    private final ReminderMvcService reminderService;
    private final UserMvcService userService;

    @Autowired
    public NotificationAdvice(ReminderMvcService reminderService, UserMvcService userService)
    {
        this.reminderService = reminderService;
        this.userService = userService;
    }

    /**
     * Счётчик непрочитанных уведовлений пользователя
     * @param principal данные пользовтаеля
     * @return число уведомлений
     */
    @ModelAttribute("notificationCount")
    public Long notificationCount(
            @AuthenticationPrincipal UserDetails principal
    )
    {
        if (principal == null)
        {
            return 0L;
        }

        UserDto user = userService.getByEmail(principal.getUsername());
        return reminderService.countActive(user.id());
    }
}
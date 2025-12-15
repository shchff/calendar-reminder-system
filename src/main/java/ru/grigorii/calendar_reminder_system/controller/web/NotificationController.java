package ru.grigorii.calendar_reminder_system.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.ReminderMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Контроллер WEB: напоминания
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController
{
    private final ReminderMvcService reminderService;
    private final UserMvcService userService;

    @Autowired
    public NotificationController(ReminderMvcService reminderService, UserMvcService userService)
    {
        this.reminderService = reminderService;
        this.userService = userService;
    }

    @GetMapping
    public String notifications(
            @AuthenticationPrincipal UserDetails principal,
            Model model
    )
    {
        UserDto user = userService.getByEmail(principal.getUsername());

        model.addAttribute(
                "reminders",
                reminderService.findActive(user.id())
        );

        return "notifications";
    }

    @PostMapping("/{id}/read")
    public String markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    )
    {
        UserDto user = userService.getByEmail(principal.getUsername());
        reminderService.markAsRead(id, user.id());

        return "redirect:/notifications";
    }
}

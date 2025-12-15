package ru.grigorii.calendar_reminder_system.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.CalendarMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Контроллер WEB: календари
 */
@Controller
@RequestMapping("/calendars")
public class CalendarsController
{

    private final CalendarMvcService calendarService;
    private final UserMvcService userService;

    @Autowired
    public CalendarsController(CalendarMvcService calendarService, UserMvcService userService)
    {
        this.calendarService = calendarService;
        this.userService = userService;
    }

    @GetMapping
    public String calendars(Model model,
                            @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        model.addAttribute("calendar",
                CalendarDto.forCreate("", "", user.id()));
        model.addAttribute("calendars",
                calendarService.findByOwner(user.id()));

        return "calendars";
    }

    @PostMapping
    public String create(@ModelAttribute CalendarDto dto)
    {
        calendarService.createCalendar(dto);
        return "redirect:/calendars?created=true";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());
        calendarService.deleteById(id, user.id());

        return "redirect:/calendars?deleted=true";
    }
}

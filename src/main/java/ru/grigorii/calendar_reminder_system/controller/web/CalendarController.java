package ru.grigorii.calendar_reminder_system.controller.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.CalendarDto;
import ru.grigorii.calendar_reminder_system.dto.EventDto;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.CalendarMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.EventMvcService;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Контроллер WEB: календарь. Приведены операции как над календарём, так и над событиями (создание, помечивание
 * как выполненные, удаление)
 */
@Controller
@RequestMapping("/calendar")
public class CalendarController
{

    private final CalendarMvcService calendarService;
    private final EventMvcService eventService;
    private final UserMvcService userService;

    @Autowired
    public CalendarController(CalendarMvcService calendarService,
                              EventMvcService eventService,
                              UserMvcService userService)
    {
        this.calendarService = calendarService;
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Страница календаря
     * @param id ID календаря
     * @param principal информация о пользователе
     * @param model модель
     * @return страница
     */
    @GetMapping("/{id}")
    public String calendarPage(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails principal,
                               Model model)
    {

        UserDto user = userService.getByEmail(principal.getUsername());
        Long userId = user.id();

        CalendarDto calendar = calendarService.findByIdForOwner(id, userId);

        model.addAttribute("calendar", calendar);
        model.addAttribute("events",
                eventService.findByCalendar(id, userId));

        model.addAttribute("event", EventDto.forCreate(id));

        return "calendar";
    }

    @PostMapping("/{id}/update")
    public String updateCalendar(@PathVariable Long id,
                                 @ModelAttribute CalendarDto dto,
                                 @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        calendarService.updateForOwner(id, dto, user.id());

        return "redirect:/calendar/{id}?updated=true";
    }


    @PostMapping("/{id}/delete")
    public String deleteCalendar(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        calendarService.deleteById(id, user.id());

        return "redirect:/calendars?deleted=true";
    }

    @PostMapping("/{id}/events")
    public String createEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") EventDto dto,
                              BindingResult result,
                              @AuthenticationPrincipal UserDetails principal,
                              Model model)
    {

        UserDto user = userService.getByEmail(principal.getUsername());
        Long userId = user.id();

        if (result.hasErrors())
        {
            model.addAttribute("calendar",
                    calendarService.findByIdForOwner(id, userId));
            model.addAttribute("events",
                    eventService.findByCalendar(id, userId));
            return "calendar";
        }

        eventService.create(dto, userId);

        return "redirect:/calendar/{id}?eventCreated=true";
    }

    @PostMapping("/{calendarId}/events/{eventId}/delete")
    public String deleteEvent(@PathVariable Long calendarId,
                              @PathVariable Long eventId,
                              @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        eventService.delete(eventId, user.id());

        return "redirect:/calendar/{calendarId}?eventDeleted=true";
    }

    @PostMapping("/{calendarId}/events/{eventId}/done")
    public String markEventDone(@PathVariable Long calendarId,
                                @PathVariable Long eventId,
                                @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        eventService.markDone(eventId, user.id());

        return "redirect:/calendar/{calendarId}";
    }

    @PostMapping("/{calendarId}/events/{eventId}/undone")
    public String markEventUndone(@PathVariable Long calendarId,
                                  @PathVariable Long eventId,
                                  @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());

        eventService.markUndone(eventId, user.id());

        return "redirect:/calendar/{calendarId}";
    }
}

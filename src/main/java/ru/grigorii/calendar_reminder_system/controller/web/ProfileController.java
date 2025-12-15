package ru.grigorii.calendar_reminder_system.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Контроллер WEB: профиль пользователя
 */
@Controller
@RequestMapping("/profile")
public class ProfileController
{

    private final UserMvcService userService;

    public ProfileController(UserMvcService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public String profile(Model model,
                          @AuthenticationPrincipal UserDetails principal)
    {

        UserDto user = userService.getByEmail(principal.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @ModelAttribute("user") @Validated UserDto dto,
                                HttpServletRequest request,
                                HttpServletResponse response)
    {

        boolean emailChanged = !principal.getUsername().equals(dto.email());

        userService.updateProfile(principal.getUsername(), dto);

        if (emailChanged)
        {
            new SecurityContextLogoutHandler()
                    .logout(request, response, null);
            return "redirect:/auth/login?emailChanged=true";
        }

        return "redirect:/profile?updated=true";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
    {

        userService.changePassword(principal.getUsername(), oldPassword, newPassword);

        new SecurityContextLogoutHandler()
                .logout(request, response, null);

        return "redirect:/auth/login?passwordChanged=true";
    }

    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails principal,
                                @RequestParam String password,
                                HttpServletRequest request,
                                HttpServletResponse response)
    {

        userService.deleteAccount(principal.getUsername(), password);

        new SecurityContextLogoutHandler()
                .logout(request, response, null);

        return "redirect:/auth/login?accountDeleted=true";
    }
}

package ru.grigorii.calendar_reminder_system.controller.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.grigorii.calendar_reminder_system.dto.UserDto;
import ru.grigorii.calendar_reminder_system.service.mvc.UserMvcService;

/**
 * Контроллер WEB: авторизация
 */
@Controller
@RequestMapping("/auth")
public class AuthController
{

    private final UserMvcService userMvcService;

    @Autowired
    public AuthController(UserMvcService userMvcService)
    {
        this.userMvcService = userMvcService;
    }

    @GetMapping("/login")
    public String loginPage()
    {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model)
    {
        model.addAttribute("dto", UserDto.empty());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("dto") UserDto dto,
            BindingResult bindingResult
    )
    {
        if (bindingResult.hasErrors())
        {
            return "auth/register";
        }

        userMvcService.register(dto);
        return "redirect:/auth/login";
    }
}
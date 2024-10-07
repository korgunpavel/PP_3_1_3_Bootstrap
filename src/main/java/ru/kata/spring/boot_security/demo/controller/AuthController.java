package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserValidator userValidator;
    private final RegistrationService registrationService;
    private final RoleService roleService;

    @Autowired
    public AuthController(UserValidator userValidator, RegistrationService registrationService, RoleService roleService) {
        this.userValidator = userValidator;
        this.registrationService = registrationService;
        this.roleService = roleService;
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") User user,
                                   ModelMap model) {
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/auth/registration";
        }
        registrationService.register(user);

        return "redirect:/auth/login";
    }
}

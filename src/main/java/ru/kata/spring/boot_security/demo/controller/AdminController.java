package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.UsersDetails;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.services.UsersDetailsService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.security.Principal;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsersDetailsService usersDetailsService;
    private final RoleService roleService;
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public AdminController(UsersDetailsService usersDetailsService, RoleService roleService, UserService userService, UserValidator userValidator) {
        this.usersDetailsService = usersDetailsService;
        this.roleService = roleService;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping("/users")
    public String users(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsersDetails usersDetails = (UsersDetails) authentication.getPrincipal();
        User currentUser = userService.loadUserByUsername(usersDetails.getUsername()).get();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", new User());
        model.addAttribute("users", usersDetailsService.findAll());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/users";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                         ModelMap model) {
        userValidator.validate(user, bindingResult);

        if (bindingErrors(user, bindingResult, model)) return "admin/users";
        usersDetailsService.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/edit/{id}")
    public String showUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           @PathVariable("id") Long id, ModelMap model) {
        if (!userService.isUsernameUnique(user.getUsername(), user.getId())) {
            bindingResult.rejectValue("username", "", "Username already exists");
        }
        if (bindingErrors(user, bindingResult, model)) return "admin/users";
        usersDetailsService.update(id, user);
        return "redirect:/admin/users";
    }

    private boolean bindingErrors(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, ModelMap model) {
        if (bindingResult.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersDetails usersDetails = (UsersDetails) authentication.getPrincipal();
            User currentUser = userService.loadUserByUsername(usersDetails.getUsername()).get();
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("user", user);
            model.addAttribute("users", usersDetailsService.findAll());
            model.addAttribute("roles", roleService.getAllRoles());
            return true;
        }
        return false;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id) {
        usersDetailsService.delete(id);
        return "redirect:/admin/users";
    }

}
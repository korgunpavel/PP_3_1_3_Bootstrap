package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
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

import java.security.Principal;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsersDetailsService usersDetailsService;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public AdminController(UsersDetailsService usersDetailsService, RoleService roleService, UserService userService) {
        this.usersDetailsService = usersDetailsService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsersDetails usersDetails = (UsersDetails) authentication.getPrincipal();
        String username = usersDetails.getUsername();
        User user = userService.loadUserByUsername(username).get();
        model.addAttribute("currentUser", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("users", usersDetailsService.findAll());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/users";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") @Valid User user, Model model) {
        usersDetailsService.save(user);
        return "redirect:/admins/users";
    }

    @PostMapping("/edit")
    public String showUser(@ModelAttribute("user") @Valid User user) {
        usersDetailsService.update(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        usersDetailsService.delete(id);
        return "redirect:/admin/users";
    }

}
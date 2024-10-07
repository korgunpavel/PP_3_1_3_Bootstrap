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
import ru.kata.spring.boot_security.demo.services.UsersDetailsService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsersDetailsService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UsersDetailsService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admins")
    public String getAdmin() {
        return "admin/admins";
    }

    @GetMapping("/users")
    public String users(ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }


    @GetMapping("/new")
    public String newUser(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/show";
    }

    @PostMapping("/show")
    public String showUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/show";
        }
        if (user.getId() == null) {
            userService.save(user);
        } else {
            userService.update(user.getId(), user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam(name = "id") Long id, ModelMap model) {
        User user = userService.show(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/show";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }

}
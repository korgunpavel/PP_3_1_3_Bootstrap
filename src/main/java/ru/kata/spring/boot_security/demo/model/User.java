package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    @NotEmpty(message = "First name should be not empty")
    @Size(min = 2, max = 20, message = "First name should be between 2 and 20 characters")
    @Pattern(regexp = "^\\p{Upper}\\p{Lower}+",
            message = "First name must begin with a capital letter and consist only of letters.")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "Last name should be not empty")
    @Size(min = 2, max = 20, message = "Last name should be between 2 and 20 characters")
    @Pattern(regexp = "^\\p{Upper}\\p{Lower}+",
            message = "Last name must begin with a capital letter and consist only of letters.")
    private String lastName;

    @Column(name = "age")
    @Min(value = 0, message = "Age must be greater than 0")
    private int age;

    @NotEmpty
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public @NotEmpty Set<Role> getRoles() {
        return roles;
    }
}

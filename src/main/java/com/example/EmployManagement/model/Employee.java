package com.example.EmployManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 65, message = "Age should not be more than 65")
    private int age;

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name is mandatory") @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is mandatory") @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Email is mandatory") @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is mandatory") @Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 65, message = "Age should not be more than 65")
    public int getAge() {
        return age;
    }

    public void setAge(@Min(value = 18, message = "Age should not be less than 18") @Max(value = 65, message = "Age should not be more than 65") int age) {
        this.age = age;
    }
}


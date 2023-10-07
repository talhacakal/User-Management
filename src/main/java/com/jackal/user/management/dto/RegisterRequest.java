package com.jackal.user.management.dto;

import com.jackal.user.management.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name can not be blank.")
    private String firstname;
    @NotBlank(message = "LastName can not be blank.")
    private String lastname;
    @Email(message = "Enter valid email.")
    private String email;
    @NotBlank(message = "Password can not be blank.")
    private String password;
    private Role role;
}

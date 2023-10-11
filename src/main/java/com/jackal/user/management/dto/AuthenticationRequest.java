package com.jackal.user.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationRequest {

    @Email(message = "Enter valid email.")
    private String email;
    @NotBlank(message = "Password can not be blank.")
    private String password;

}

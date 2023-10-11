package com.jackal.user.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Password can not be blank.")
    private String currentPassword;
    @NotBlank(message = "Password can not be blank.")
    private String newPassword;
    @NotBlank(message = "Password can not be blank.")
    private String confirmationPassword;

}

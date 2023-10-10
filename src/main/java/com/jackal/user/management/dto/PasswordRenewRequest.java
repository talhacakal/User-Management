package com.jackal.user.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PasswordRenewRequest {

    @NotBlank(message = "Password can not be blank.")
    private String newPassword;
    @NotBlank(message = "Password can not be blank.")
    private String confirmationPassword;

}

package com.jackal.user.management.dto;

import com.jackal.user.management.utils.annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordRenewRequest {

    @ValidPassword
    private String newPassword;
    private String confirmationPassword;

}

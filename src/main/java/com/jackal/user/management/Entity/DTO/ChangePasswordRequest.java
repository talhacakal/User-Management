package com.jackal.user.management.Entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;

}

package com.jackal.user.management.Entity.DTO;

import com.jackal.user.management.User.AppUser;
import com.jackal.user.management.User.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String name;
    private String lastname;
    private String email;
    private Role role;

    public UserDTO(AppUser user) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}

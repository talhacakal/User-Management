package com.jackal.user.management.event;

import com.jackal.user.management.user.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ClearUserTokensEvent extends ApplicationEvent {

    private final AppUser user;
    private final String jwt;
    private final String refresh;

    public ClearUserTokensEvent(AppUser user, String jwt, String refresh) {
        super(user);
        this.user = user;
        this.jwt = jwt;
        this.refresh = refresh;
    }
}

package com.jackal.user.management.event;

import com.jackal.user.management.user.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final AppUser user;
    private final String appUrl;

    public RegistrationCompleteEvent(AppUser user, String applicationUrl) {
        super(user);
        this.user = user;
        this.appUrl = applicationUrl;
    }
}

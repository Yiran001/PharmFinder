package com.pharm.pharmfinder.mail.pw_reset;

import com.pharm.pharmfinder.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnPasswordResetEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    private String newPassword;

    public OnPasswordResetEvent(String appUrl, Locale locale, User user, String newPassword) {
        super(user);
        this.appUrl = appUrl;
        this.locale = locale;
        this.user = user;
        this.newPassword = newPassword;
    }
}

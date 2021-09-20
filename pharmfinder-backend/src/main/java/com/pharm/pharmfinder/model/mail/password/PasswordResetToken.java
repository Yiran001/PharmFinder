package com.pharm.pharmfinder.model.mail.password;

import com.pharm.pharmfinder.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;



//https://www.baeldung.com/spring-security-registration-i-forgot-my-password
@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    public PasswordResetToken(String token, User user, String newPassword) {
        this.token = token;
        this.user = user;
        this.newPassword = newPassword;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String newPassword;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

}

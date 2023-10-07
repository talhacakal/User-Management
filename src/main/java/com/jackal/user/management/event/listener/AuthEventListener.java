package com.jackal.user.management.event.listener;

import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.Token;
import com.jackal.user.management.token.TokenRepository;
import com.jackal.user.management.user.AppUser;
import com.jackal.user.management.event.ClearUserTokensEvent;
import com.jackal.user.management.event.RegistrationCompleteEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthEventListener {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    @EventListener
    @Async
    public void emailVerificationListener(RegistrationCompleteEvent event) throws MessagingException, UnsupportedEncodingException {
        this.confirmRegistration(event);
    }
    @Async
    @EventListener
    public void clearUserTokensEventListener(ClearUserTokensEvent event) {
        List<Token> tokenList = this.tokenRepository
                .findByUser_Email(event.getUser().getEmail())
                .stream()
                .filter(item -> !item.getToken().equals(event.getJwt()) && !item.getToken().equals(event.getRefresh()))
                .toList();

        this.tokenRepository.deleteAll(tokenList);

    }
    private void confirmRegistration(RegistrationCompleteEvent event) throws MessagingException, UnsupportedEncodingException {
        AppUser user = event.getUser();
        String token = this.jwtService.generateEmailVerificationToken(user);
        String url = event.getAppUrl() + "/api/v1/auth/signup/verifyEmail?token=" + token;

        String subject = "Registration Confirmation";
        String mailContent = "<p> Hi, "+ user.getName() + user. getLastname() + ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";


        MimeMessage message = mailSender.createMimeMessage();
        var email = new MimeMessageHelper(message);
        email.setFrom("usermanagement@gmail.com","User Registration Service");
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(mailContent,true);
        mailSender.send(message);
    }
}

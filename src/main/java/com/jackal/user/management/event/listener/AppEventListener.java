package com.jackal.user.management.event.listener;

import com.jackal.user.management.event.ClearUserTokensEvent;
import com.jackal.user.management.event.ForgotPasswordEvent;
import com.jackal.user.management.event.RegistrationCompleteEvent;
import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.Token;
import com.jackal.user.management.token.TokenRepository;
import com.jackal.user.management.token.TokenType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppEventListener {

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
    @Async
    @EventListener
    public void sendForgotPasswordEmail(ForgotPasswordEvent event) throws MessagingException, UnsupportedEncodingException {
        var user = event.getUser();
        var extraClaims = new HashMap<String, Object>();
        extraClaims.put("TokenType", TokenType.PASSWORD_REFRESH);

        String token = this.jwtService.generateTokenWithExtraClaims(user, extraClaims);
        String url = event.getAppUrl() + "/PasswordRenewPage?token="+token;

        String subject = "Reset Password";
        String mailContent = "<p> Hi, "+ user.getName() + ", </p>"+
                "<p>Please, follow the link below to reset your password.</p>"+
                "<a href=\"" +url+ "\">Reset Password</a>"+
                "<br> Users Registration Portal Service";

        this.sendEmail(user.getEmail(), subject, mailContent);

    }
    private void confirmRegistration(RegistrationCompleteEvent event) throws MessagingException, UnsupportedEncodingException {
        var user = event.getUser();
        var extraClaims = new HashMap<String, Object>();
        extraClaims.put("TokenType", TokenType.VERIFICATION);
        String token = this.jwtService.generateTokenWithExtraClaims(user, extraClaims);
        String url = event.getAppUrl() + "/api/v1/auth/signup/verifyEmail?token=" + token;

        String subject = "Registration Confirmation";
        String mailContent = "<p> Hi, "+ user.getName() + user. getLastname() + ", </p>"+
                "<p>Thank you for registering with us,"+
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";

        this.sendEmail(user.getEmail(), subject, mailContent);
    }
    private void sendEmail(String to, String subject, String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var email = new MimeMessageHelper(message);
        email.setFrom("usermanagement@gmail.com","User Service");
        email.setTo(to);
        email.setSubject(subject);
        email.setText(mailContent,true);
        mailSender.send(message);
    }
}

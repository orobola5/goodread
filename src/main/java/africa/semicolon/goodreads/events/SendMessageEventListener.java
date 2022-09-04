package africa.semicolon.goodreads.events;

import africa.semicolon.goodreads.Services.EmailService;
import africa.semicolon.goodreads.models.MailResponse;
import africa.semicolon.goodreads.models.VerificationMessageRequest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
@Component
@Slf4j
public class SendMessageEventListener {
    @Qualifier("mailgun_sender")
    private final EmailService emailService;
    private final Environment env;
    private final TemplateEngine templateEngine;

    public SendMessageEventListener(EmailService emailService, Environment env, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.env = env;
        this.templateEngine = templateEngine;
    }

    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) throws UnirestException, ExecutionException, InterruptedException {
        VerificationMessageRequest messageRequest = (VerificationMessageRequest) event.getSource();

        String verificationLink = messageRequest.getDomainUrl()+"api/v1/auth/verify/"+messageRequest.getVerificationToken();

        log.info("Message request --> {}",messageRequest);
        Context context = new Context();
        context.setVariable("user_name", messageRequest.getUsersFullName().toUpperCase());
        context.setVariable("verification_token", verificationLink);
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")){
            log.info("Message Event -> {}", event.getSource());
            messageRequest.setBody(templateEngine.process("registration_verification_mail.html", context));
            MailResponse mailResponse = emailService.sendHtmlMail(messageRequest).get();
            log.info("Mail Response --> {}", mailResponse);
        } else{
            messageRequest.setBody(verificationLink);
            MailResponse mailResponse = emailService.sendSimpleMail(messageRequest).get();
            log.info("Mail Response --> {}", mailResponse);
        }
    }
}

package com.sgcor.shopply.message;

import com.sgcor.shopply.Application;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final RabbitTemplate rabbitTemplate;
    private final JavaMailSender emailSender;

    @Value("${email.queue}")
    private String emailQueue;

    public void sendEmail(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(emailQueue, emailMessage);
    }

    @RabbitListener(queues = "${email.queue}")
    public void processSendEmail(EmailMessage emailMessage) {
        logger.info("Received email message: {}", emailMessage);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@shopply.com");
        message.setTo(emailMessage.getTo());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getBody());
        emailSender.send(message);
    }

    public void processSendEmail(EmailMessage emailMessage, String attachment, String attachmentName) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@shopply.com");
        helper.setTo(emailMessage.getTo());
        helper.setSubject(emailMessage.getSubject());
        helper.setText(emailMessage.getBody());

        FileSystemResource file = new FileSystemResource(new File(attachment));
        helper.addAttachment(attachmentName, file);

        emailSender.send(message);
    }
}

package com.minhhai.social_network.service;

import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.util.enums.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Async
    public void sendEmail(String to, String subject, String content, MultipartFile[] files) {
        try {
            log.info("-------------- Email is sending ... -------------");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom, "Minh Hai Nguyen");
            helper.setTo(to);

            if (files != null) {
                for (MultipartFile file : files) {
                    helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()),
                            new ByteArrayResource(file.getBytes()));
                }
            }

            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("------ Email has sent to successfully, recipients: {} ----------", to);
        } catch (Exception e) {
            log.error("----------- Sending email was failure, message={} ----------- ", e.getMessage(), e);
            throw new AppException(ErrorCode.SEND_MAIL_FAILED);
        }

    }

}

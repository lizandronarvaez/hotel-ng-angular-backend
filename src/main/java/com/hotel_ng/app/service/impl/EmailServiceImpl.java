package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;
import com.hotel_ng.app.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${mailSender.username}")
    static String username;

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendEmail(RequestFormQuestionDTO formQuestionDTO) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setFrom(username, "Hotel AngularNG <no-reply@hotelng.com>");
            messageHelper.setTo(formQuestionDTO.getEmail().trim().toLowerCase());
            messageHelper.setSubject("Tu consulta fue recibida");

            String htmlContent = "<p>Nos pondremos en contacto a la mayor brevedad posible</p>\n" + "Motivo de la consulta: " + formQuestionDTO.getMessage();
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (MessagingException e) {
            log.error("Hubo un error al enviar el correo a {}: {}", formQuestionDTO.getEmail(), e.getMessage(), e);
        }
    }
}

package com.hotel_ng.app.service.impl;

import com.hotel_ng.app.dto.UserDto;
import com.hotel_ng.app.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${mailSender.username}")
    private String username;

    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    @Async
    public void sendEmail(UserDto userDto) {
        logger.info("Iniciando envío asíncrono de correo a: {}", userDto.getEmail());
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setFrom(username, "Hotel AngularNG <no-reply@hotelng.com>");
            messageHelper.setTo(userDto.getEmail().trim());
            messageHelper.setSubject("Tu consulta fue recibida");

            String htmlContent = "<p>Nos pondremos en contacto a la mayor brevedad posible</p>\n"
                    + "Motivo de la consulta: " + userDto.getMessage();
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

            logger.info("Correo enviado exitosamente a: {}", userDto.getEmail().trim());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (MessagingException e) {
            logger.error(
                    "Hubo un error al enviar el correo a {}: {}",
                    userDto.getEmail(),
                    e.getMessage(), e
            );
        }
    }
}

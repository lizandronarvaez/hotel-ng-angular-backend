package com.hotel_ng.app.service.interfaces;

import com.hotel_ng.app.dto.request.RequestFormQuestionDTO;

public interface EmailService {
    void sendEmail(RequestFormQuestionDTO requestFormQuestionDTO);
}

package com.hotel_ng.app.uploads.cloudDinary.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudDinaryService {
    String uploadImage(MultipartFile file, String nameFolder) throws IOException;
    void verifyFileFormat(String extFile);

}

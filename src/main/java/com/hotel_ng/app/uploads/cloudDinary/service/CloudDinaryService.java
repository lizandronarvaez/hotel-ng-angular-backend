package com.hotel_ng.app.uploads.cloudDinary.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudDinaryService {
    String uploadImage(MultipartFile file, String nameFolder);

    String[] getFileNameParts(MultipartFile file);

    void verifyFileSize(MultipartFile file);

    void verifyFileFormat(String extFile);

    String generateNewFileName(String nameFile, String extFile);

}

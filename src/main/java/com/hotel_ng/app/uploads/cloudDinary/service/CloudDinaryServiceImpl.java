package com.hotel_ng.app.uploads.cloudDinary.service;

import java.io.IOException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;

@RequiredArgsConstructor
@Service
public class CloudDinaryServiceImpl implements CloudDinaryService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpeg", "jpg", "png", "webp");

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String nameFolder) {
        try {
            String[] originalFileName = getFileNameParts(file);
            String extFile = originalFileName[1];

            verifyFileSize(file);
            verifyFileFormat(extFile);

            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", nameFolder);
            @SuppressWarnings("rawtypes")
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);

            return (String) uploadedFile.get("secure_url");
        } catch (IOException e) {
            return ("Error al cargar el archivo: " + e.getMessage());
        }
    }

    @Override
    public String[] getFileNameParts(MultipartFile file) {
        return file.getOriginalFilename().split("\\.");
    }

    @Override
    public void verifyFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE)
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
    }

    @Override
    public void verifyFileFormat(String extFile) {
        if (!ALLOWED_EXTENSIONS.contains(extFile.toLowerCase()))
            throw new MultipartException("¡El formato de archivo no es válido!");
    }

    @Override
    public String generateNewFileName(String nameFile, String extFile) {
        return nameFile + UUID.randomUUID() + "." + extFile;
    }

}

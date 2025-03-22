package com.hotel_ng.app.uploads.cloudDinary.service;

import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@RequiredArgsConstructor
@Service
public class CloudDinaryServiceImpl implements CloudDinaryService {

    private static final List<String> EXTENSION_IMAGE_VALID = Arrays.asList("jpeg", "jpg", "png", "webp");

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String nameFolder) throws IOException {

            @SuppressWarnings("null")
            String extImage = file.getOriginalFilename().split("\\.")[1];
            verifyFileFormat(extImage);
            var uploadedFile = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", nameFolder));
            return cloudinary.url().secure(true).generate((String) uploadedFile.get("public_id"));
    }

    @Override
    public void verifyFileFormat(String extFile) {
        if (!EXTENSION_IMAGE_VALID.contains(extFile.toLowerCase()))
            throw new MultipartException("¡El formato de archivo no es válido!");
    }

}

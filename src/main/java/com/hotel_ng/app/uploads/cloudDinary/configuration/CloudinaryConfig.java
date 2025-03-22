package com.hotel_ng.app.uploads.cloudDinary.configuration;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import org.springframework.context.annotation.*;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final CloudinaryProperties cloudinaryProperties;

    @Bean
    Cloudinary cloudinary() {
        HashMap<String,String> config=new HashMap<>();
        config.put("cloud_name", cloudinaryProperties.getCloudName());
        config.put("api_key", cloudinaryProperties.getApiKey());
        config.put("api_secret", cloudinaryProperties.getApiSecret());
        return new Cloudinary(config);
    }
}

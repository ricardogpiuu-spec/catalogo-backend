package com.rcsistema.catalogo.Catalogo.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dyvec4jx4",
                "api_key", "241176949895529",
                "api_secret", "CTOO6iqB4-VJhXiZlqWhHYoP4Is"
        ));
    }
}

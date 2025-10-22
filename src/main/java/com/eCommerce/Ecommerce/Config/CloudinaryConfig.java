package com.eCommerce.Ecommerce.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
// import com.cloudinary.utils.ObjectUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class CloudinaryConfig {

    
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}") 
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;



    @Bean
    public Cloudinary cloudinary() {
       
        Map map = new HashMap();
        map.put("cloud_name", cloudName);
        map.put("api_key", apiKey); 
        map.put("api_secret", apiSecret);

        return new Cloudinary(map);
    }
}

             


package com.zalo.Spring_Zalo;

import com.cloudinary.Cloudinary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


@SpringBootApplication(scanBasePackages = "com.zalo.Spring_Zalo")
public class SpringZaloApplication extends SpringBootServletInitializer{
	public static void main(String[] args) {
		SpringApplication.run(SpringZaloApplication.class, args);
	}
	@Bean
	public Cloudinary getCloudinary(){
		Map config = new HashMap<>();
		ResourceBundle infomation =  ResourceBundle.getBundle("application");
		config.put("cloud_name",infomation.getString("cloud_name"));
		config.put("api_key",infomation.getString("api_key"));
		config.put("api_secret",infomation.getString("api_secret"));
		config.put("secure",true);
		return new Cloudinary(config);
	}
}

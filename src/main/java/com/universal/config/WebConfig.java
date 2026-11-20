package com.universal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.universal.service.FileStorageService;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageService files;

	public WebConfig(FileStorageService files) {
		this.files = files;
	}

	/** Serves the uploaded photos from the upload folder at /uploads/** */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(files.getRoot().toUri().toString())
				.setCachePeriod(3600);
	}
}

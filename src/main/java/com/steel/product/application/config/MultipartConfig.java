package com.steel.product.application.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(20 * 1024 * 1024);
        resolver.setMaxUploadSizePerFile(3 * 1024 * 1024);
        resolver.setMaxInMemorySize(0);
        resolver.setPreserveFilename(true);
        return resolver;
    }

//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		factory.setMaxFileSize(DataSize.ofMegabytes(3));
//		factory.setMaxRequestSize(DataSize.ofMegabytes(20));
//		return factory.createMultipartConfig();
//	}
}

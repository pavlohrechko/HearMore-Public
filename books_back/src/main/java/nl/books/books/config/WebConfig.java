package nl.books.books.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/audio_files/**")
                .addResourceLocations("file:audio_files/") // Make sure this is the correct location on your server
                .setCachePeriod(0); // Disable caching during development

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:images/"); // Ensure this is the correct directory
    }
}


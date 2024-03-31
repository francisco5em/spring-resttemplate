/**
 * 
 */
package com.francisco5em.springresttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Creado por Francisco E.
 */
@Configuration
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    String host_Url;

    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplaConfig) {
        assert host_Url != null;

        RestTemplateBuilder builder = restTemplaConfig.configure(new RestTemplateBuilder());
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(host_Url);

        return builder.uriTemplateHandler(uriBuilderFactory);
    }
/*
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }*/
}

package com.daugherty.pam


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Configuration
class ApplicationConfig {
  @Bean
  RestTemplate restTemplate() {
    new RestTemplate(new HttpComponentsClientHttpRequestFactory())
  }
}

package application.tracker.service.config;

import application.tracker.service.events.ApplicationStatusEvent;
import application.tracker.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AppConfig {

    private final CustomUserDetailsService userDetailsService;
    @Value("${app.services.match-service-url}")
    private String matchServiceUrl;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        // Create the provider and set UserDetailsService as an argument in Dao because of spring boot 3+ version
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean("userServiceClient")
    public RestClient userRestClient(RestClient.Builder builder){
        return builder.baseUrl("lb://USER-SERVICE").build();
    }

    @Bean("matchServiceClient")
    public RestClient matchRestClient(RestClient.Builder builder){
        return builder.baseUrl("lb://MATCH-SERVICE").build();
    }


    @Bean 
    public ProducerFactory<String, ApplicationStatusEvent> producerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);

    }

    @Bean
    public KafkaTemplate<String, ApplicationStatusEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }


}
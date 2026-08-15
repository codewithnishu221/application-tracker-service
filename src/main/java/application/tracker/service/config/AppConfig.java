package application.tracker.service.config;

import application.tracker.service.events.ApplicationStatusEvent;
import application.tracker.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Primary;
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
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder(ObjectProvider<RestClientCustomizer> customizerProvider) {
        RestClient.Builder builder = RestClient.builder();
        customizerProvider.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder;
    }
    @Bean("loadBalancedBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder(ObjectProvider<RestClientCustomizer> customizerProvider) {
        RestClient.Builder builder = RestClient.builder();
        customizerProvider.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder;
    }

    @Bean("userRestClient")
    public RestClient userRestClient(@Qualifier("loadBalancedBuilder") RestClient.Builder builder) {
        return builder.clone().baseUrl("http://USER-SERVICE").build();
    }

    @Bean("matchRestClient")
    public RestClient matchRestClient(@Qualifier("loadBalancedBuilder") RestClient.Builder builder) {
        return builder.clone().baseUrl("http://MATCH-SERVICE").build();
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
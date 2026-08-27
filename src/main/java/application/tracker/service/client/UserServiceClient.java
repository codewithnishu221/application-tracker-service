package application.tracker.service.client;

import application.tracker.service.dto.UserDetailsDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Qualifier("userRestClient") RestClient restClient){
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserDetailsFallback")
    public UserDetailsDto getUserDetails(Long userId){
        log.info("Calling User Service for userId: {}", userId);
        UserDetailsDto response = restClient.get()
                .uri("http://USER-SERVICE/api/users/{userId}", userId)
//                .header("Authorization", forwardedtoken)
                .retrieve()
                .body(UserDetailsDto.class);
        return  response;
    }

    public UserDetailsDto getUserDetailsFallback(Long userId, String token, Exception ex){
        log.warn("Circuit braker triggered for User Service. " +
                "UserId: {}. Reason: {}", userId, ex.getMessage());
        return null;
    }

}

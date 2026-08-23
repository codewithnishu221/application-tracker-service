package application.tracker.service.client;

import application.tracker.service.dto.UserDetailsDto;
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

    public UserDetailsDto getUserDetails(Long userId){
//        String forwardedtoken = token.startsWith("Bearer ") ? token : "Bearer " + token;
        UserDetailsDto response = restClient.get()
                .uri("http://USER-SERVICE/api/users/{userId}", userId)
//                .header("Authorization", forwardedtoken)
                .retrieve()
                .body(UserDetailsDto.class);
        return  response;
    }

}

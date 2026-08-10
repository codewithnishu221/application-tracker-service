package application.tracker.service.client;

import application.tracker.service.dto.MatchScoreRequest;
import application.tracker.service.dto.MatchScoreResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class MatchServiceClient {

    private final RestClient restClient;
    public MatchServiceClient(@Qualifier("matchServiceClient") RestClient restClient) {
        this.restClient = restClient;
    }

   public MatchScoreResponse getMatchScore(String jobDescription, Long resumeId, String authToken) {
       try {
           MatchScoreRequest requestPayload = new MatchScoreRequest(resumeId, jobDescription);
           MatchScoreResponse response = restClient.post()
                   .uri("/api/match/score")
                   .header("Authorization", authToken)
                   .body(requestPayload)
                   .retrieve()
                   .body(MatchScoreResponse.class);
           return response;
       } catch (Exception e) {
           return null;
       }

   }

   
}

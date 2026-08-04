package application.tracker.service.client;

import application.tracker.service.dto.MatchScoreRequest;
import application.tracker.service.dto.MatchScoreResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@AllArgsConstructor
public class MatchServiceClient {

    private final RestClient restClient;
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

package application.tracker.service.client;

import application.tracker.service.dto.MatchScoreRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@AllArgsConstructor
public class MatchServiceClient {

    private final RestClient restClient;
   public Double getMatchScore(String jobDescription, Long resumeId, String authToken) {
       try {
           MatchScoreRequest requestPayload = new MatchScoreRequest(resumeId, jobDescription);
           Double matchScore = restClient.post()
                   .uri("/api/match/score")
                   .header("Authorization", authToken)
                   .body(requestPayload)
                   .retrieve()
                   .body(Double.class);
           return matchScore;
       } catch (Exception e) {
           return null;
       }

   }
}

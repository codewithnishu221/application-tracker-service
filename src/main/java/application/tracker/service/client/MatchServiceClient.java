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

    public MatchServiceClient(@Qualifier("matchRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public MatchScoreResponse getMatchScore(String jobDescription, Long resumeId, String authToken) {
        try {
            // Ensure the token has the "Bearer " prefix before sending
            String formattedToken = authToken.startsWith("Bearer ") ? authToken : "Bearer " + authToken;

            MatchScoreRequest requestPayload = new MatchScoreRequest(resumeId, jobDescription);
            MatchScoreResponse response = restClient.post()
                    .uri("/api/match/score")
                    .header("Authorization", formattedToken) // <-- Use the formatted token here
                    .body(requestPayload)
                    .retrieve()
                    .body(MatchScoreResponse.class);
            return response;
        } catch (Exception e) {
            log.error("CRITICAL ERROR calling Match Service: {}", e.getMessage(), e);
            return null;
        }
    }
}
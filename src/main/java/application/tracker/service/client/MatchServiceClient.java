package application.tracker.service.client;

import application.tracker.service.dto.MatchScoreRequest;
import application.tracker.service.dto.MatchScoreResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

    @CircuitBreaker(name = "matchService", fallbackMethod = "matchScoreFallback")
    @Retry(name= "matchService")
    public MatchScoreResponse getMatchScore(String jobDescription, Long resumeId, String authToken) {
        log.info("Calling Match Service for resumeId: {}", resumeId);            String formattedToken = authToken.startsWith("Bearer ") ? authToken : "Bearer " + authToken;

            MatchScoreRequest requestPayload = new MatchScoreRequest(resumeId, jobDescription);
            MatchScoreResponse response = restClient.post()
                    .uri("/api/match/score")
                    .header("Authorization", formattedToken) // <-- Use the formatted token here
                    .body(requestPayload)
                    .retrieve()
                    .body(MatchScoreResponse.class);
            return response;

    }

    public MatchScoreResponse matchScoreFallback(String jobDescription, Long resumeId,
                                                 String authToken, Exception ex){
        log.warn("Circuit breaker triggered for Match Service. "+
                "ResumeId: {}. Reason: {}", resumeId, ex.getMessage());
        return null;
    }
}
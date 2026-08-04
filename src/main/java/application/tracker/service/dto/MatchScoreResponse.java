package application.tracker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MatchScoreResponse {
    private Double score;
    private String explanation;
    private List<String> missingSkills;

}

package application.tracker.service.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReuseRecommendation {

    private boolean shouldReuse;
    private Double similarityScore;
    @Nullable
    private Long recommendedResumeId;
    private String matchedJobTitle;
    private String matchedCompanyName;
    private String message;

}

package application.tracker.service.dto;

import application.tracker.service.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Double matchScore;
    private Long resumeId;
    private String description;
    private String prepLink;
    private String message;
}

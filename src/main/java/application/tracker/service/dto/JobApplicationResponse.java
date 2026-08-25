package application.tracker.service.dto;

import application.tracker.service.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse implements Serializable {
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
    private static final long serialVersionUID =1L;
}

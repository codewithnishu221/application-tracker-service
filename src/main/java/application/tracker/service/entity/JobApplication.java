package application.tracker.service.entity;

import application.tracker.service.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="job_applications", indexes = {
        @Index(name = "idx_user_id", columnList = "userId")
})
public class JobApplication {

    @Id
    private Long id;
    @Id
    private Long userId;
    private String companyName;
    private String jobTitle;
    @Column(columnDefinition = "TEXT")
    private String jobDescription;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Long resumeId;
    private double matchScore;

}

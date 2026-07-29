package application.tracker.service.entity;

import application.tracker.service.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="job_applications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    @Column(columnDefinition = "TEXT")
    private String jobDescription;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Long resumeId;
    private Double matchScore;

}

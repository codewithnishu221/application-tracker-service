package application.tracker.service.entity;

import application.tracker.service.enums.ApplicationStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="job_applications", indexes = {
        @Index(name = "idx_job_app_user_id", columnList = "userId"),
        @Index(name = "idx_job_app_user_status", columnList = "userId, status"),
        @Index(name = "idx_job_app_status_applied_at" , columnList = "status,appliedAt"),
        @Index(name = "idx_job_app_interview_date", columnList = "interviewDate")
})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    @Column(name = "appliedAt")
    private LocalDateTime appliedAt;
    @Column(name = "resume_id")
    private Long resumeId;
    @Column(name = "match_score")
    private Double matchScore;
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "jd_embedding", columnDefinition = "vector(1536)") // match your Ollama model dimension (e.g., 768, 1536, or 384)
    private float[] jdEmbedding;
    @Nullable
    @Column(name = "interview_date")
    private LocalDate interviewDate;
    private LocalDateTime lastStatusUpdatedAt;
}

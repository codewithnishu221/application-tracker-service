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
        @Index(name = "idx_user_id", columnList = "userId")
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
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "jd_embedding", columnDefinition = "vector(1536)") // match your Ollama model dimension (e.g., 768, 1536, or 384)
    private float[] jdEmbedding;
    @Nullable
    private LocalDate interviewDate;
    private LocalDateTime lastStatusUpdatedAt;
}

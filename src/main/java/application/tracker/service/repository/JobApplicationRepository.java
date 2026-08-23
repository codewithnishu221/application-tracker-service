package application.tracker.service.repository;

import application.tracker.service.entity.JobApplication;
import application.tracker.service.exceptions.ApplicationNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    JobApplication findByUserIdAndId(Long userId, Long id) throws ApplicationNotFoundException;

    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByUserIdAndJdEmbeddingIsNotNull(Long userId);
    @Query("SELECT j from JobApplication j Where j.status = 'APPLIED'" +
    "AND j.appliedAt <:cutoffDate")
    List<JobApplication> findStaleApplication(@Param("cutoffDate")LocalDateTime cutoffDate);

    @Query("Select j From JobApplication j Where j.status = 'INTERVIEW_SCHEDULED' " +
    "AND j.interviewDate = : tomorrow")
    List<JobApplication> findUpcomingInterviews(@Param("tomorrow")LocalDate tomorrow);
}

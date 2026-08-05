package application.tracker.service.repository;

import application.tracker.service.entity.JobApplication;
import application.tracker.service.exceptions.ApplicationNotFoundException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    JobApplication findByUserIdAndId(Long userId, Long id) throws ApplicationNotFoundException;

    List<JobApplication> findByUserId(Long userId);

}

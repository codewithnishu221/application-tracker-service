package application.tracker.service.service;

import application.tracker.service.dto.JobApplicationRequest;
import application.tracker.service.dto.JobApplicationResponse;
import application.tracker.service.dto.ReuseRecommendation;
import application.tracker.service.dto.UpdateStatusRequest;
import application.tracker.service.exceptions.ApplicationNotFoundException;
import org.springframework.data.domain.Page;

public interface JobApplicationService {

    JobApplicationResponse createApplication(JobApplicationRequest request, Long userId, String token);
    Page<JobApplicationResponse> getAllApplications(Long userId, int page, int size);
    JobApplicationResponse getApplicationById(Long id, Long userId) throws ApplicationNotFoundException;
    JobApplicationResponse updateStatus(Long id, Long userId, UpdateStatusRequest request) throws ApplicationNotFoundException;
    void deleteApplication(Long id, Long userId) throws IllegalAccessException;
    ReuseRecommendation checkReuseRecommendation(String newJdText, Long userId);
    void generateAndSaveEmbedding(Long applicationId, String jobDesciption);


}

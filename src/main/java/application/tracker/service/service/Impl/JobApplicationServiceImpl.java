package application.tracker.service.service.Impl;

import application.tracker.service.dto.JobApplicationRequest;
import application.tracker.service.dto.JobApplicationResponse;
import application.tracker.service.dto.UpdateStatusRequest;
import application.tracker.service.entity.JobApplication;
import application.tracker.service.enums.ApplicationStatus;
import application.tracker.service.exceptions.ApplicationNotFoundException;
import application.tracker.service.repository.JobApplicationRepository;
import application.tracker.service.service.JobApplicationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public JobApplicationResponse createApplication(JobApplicationRequest request, Long userId) {
        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle(request.getJobTitle());
        jobApp.setAppliedAt(LocalDateTime.now());
        jobApp.setCompanyName(request.getCompanyName());
        jobApp.setStatus(ApplicationStatus.APPLIED);
        jobApp.setJobDescription(jobApp.getJobDescription());
        jobApp.setUserId(userId);
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(jobApp.getId());
        response.setCompanyName(jobApp.getCompanyName());
        response.setAppliedAt(jobApp.getAppliedAt());
        response.setJobTitle(jobApp.getJobTitle());
        response.setUserId(jobApp.getUserId());
        response.setStatus(jobApp.getStatus());
        return response;
    }

    @Override
    public Page<JobApplicationResponse> getAllApplications(Long userId, int page, int size) {
        return null;
    }

    @Override
    public JobApplicationResponse getApplicationById(Long id, Long userId) throws ApplicationNotFoundException {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(id, userId);
        JobApplicationResponse jobResponse = new JobApplicationResponse();
        jobResponse.setStatus(jobApplication.getStatus());
        jobResponse.setDescription(jobApplication.getJobDescription());
        jobResponse.setMatchScore(jobApplication.getMatchScore());
        jobResponse.setJobTitle(jobApplication.getJobTitle());
        jobResponse.setAppliedAt(jobApplication.getAppliedAt());
        jobResponse.setCompanyName(jobResponse.getCompanyName());
        jobResponse.setResumeId(jobResponse.getResumeId());
        jobResponse.setId(id);
        jobResponse.setUserId(userId);
    return jobResponse;
    }

    @Override
    public JobApplicationResponse updateStatus(Long id, Long userId, UpdateStatusRequest request) {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(id, userId);
        jobApplication.setStatus(request.getStatus());
        JobApplicationResponse jobAppResponse = new JobApplicationResponse();
        jobAppResponse.setStatus(jobApplication.getStatus());
        jobAppResponse.setId(id);
        return jobAppResponse;
    }

    @Override
    public void deleteApplication(Long id, Long userId) throws IllegalAccessException {
        JobApplication jobApp = jobApplicationRepository.findById(id).orElseThrow(()-> new ApplicationNotFoundException("Application not found!"));

        if(jobApp.getUserId()!= userId){
            throw new IllegalAccessException("You do not have permission to delete this application. ");
        }else {
            jobApplicationRepository.deleteById(id);
        }


    }
}

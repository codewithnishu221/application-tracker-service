package application.tracker.service.service.Impl;

import application.tracker.service.client.MatchServiceClient;
import application.tracker.service.dto.JobApplicationRequest;
import application.tracker.service.dto.JobApplicationResponse;
import application.tracker.service.dto.MatchScoreResponse;
import application.tracker.service.dto.UpdateStatusRequest;
import application.tracker.service.entity.JobApplication;
import application.tracker.service.enums.ApplicationStatus;
import application.tracker.service.events.ApplicationStatusEvent;
import application.tracker.service.exceptions.ApplicationNotFoundException;
import application.tracker.service.repository.JobApplicationRepository;
import application.tracker.service.service.JobApplicationService;
import application.tracker.service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final MatchServiceClient matchServiceClient;
    private final KafkaProducerService kafkaProducerService;
    @Transactional
    @Override
    public JobApplicationResponse createApplication(JobApplicationRequest request, Long userId, String token) {
        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle(request.getJobTitle());
        jobApp.setAppliedAt(LocalDateTime.now());
        jobApp.setCompanyName(request.getCompanyName());
        jobApp.setStatus(ApplicationStatus.APPLIED);
        jobApp.setJobDescription(request.getJobDescription());
        jobApp.setUserId(userId);
        JobApplication savedApp = jobApplicationRepository.save(jobApp);
        if(request.getResumeId()!= null) {
            savedApp.setResumeId(request.getResumeId());
            MatchScoreResponse response = matchServiceClient.getMatchScore(
                    request.getJobDescription(),
                    request.getResumeId(),
                    token
            );
            if (response != null){
                savedApp.setMatchScore(response.getScore());
                jobApplicationRepository.save(savedApp);
            }

        }
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(savedApp.getId());
        response.setCompanyName(savedApp.getCompanyName());
        response.setAppliedAt(savedApp.getAppliedAt());
        response.setJobTitle(savedApp.getJobTitle());
        response.setUserId(savedApp.getUserId());
        response.setStatus(savedApp.getStatus());
        return response;
    }

    @Override
    public Page<JobApplicationResponse> getAllApplications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("appliedAt").descending());
        Page<JobApplication> jobAppPage = jobApplicationRepository.findByUserId(userId, pageable);
        Page<JobApplicationResponse> responsePage = jobAppPage.map(jobApp->{
            JobApplicationResponse response = new JobApplicationResponse();
            response.setId(jobApp.getId());
            response.setUserId(jobApp.getUserId());
            response.setCompanyName(jobApp.getCompanyName());
            response.setJobTitle(jobApp.getJobTitle());
            response.setAppliedAt(jobApp.getAppliedAt());
            response.setStatus(jobApp.getStatus());
            response.setMatchScore(jobApp.getMatchScore());
            response.setResumeId(jobApp.getResumeId());
            return response;
        });
        return responsePage;
    }

    @Override
    public JobApplicationResponse getApplicationById(Long id, Long userId) throws ApplicationNotFoundException {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(userId,id);
        JobApplicationResponse jobResponse = new JobApplicationResponse();
        jobResponse.setStatus(jobApplication.getStatus());
        jobResponse.setDescription(jobApplication.getJobDescription());
        jobResponse.setMatchScore(jobApplication.getMatchScore());
        jobResponse.setJobTitle(jobApplication.getJobTitle());
        jobResponse.setAppliedAt(jobApplication.getAppliedAt());
        jobResponse.setCompanyName(jobApplication.getCompanyName());
        jobResponse.setResumeId(jobApplication.getResumeId());
        jobResponse.setId(id);
        jobResponse.setUserId(userId);
    return jobResponse;
    }

    @Transactional
    @Override
    public JobApplicationResponse updateStatus(Long id, Long userId, UpdateStatusRequest request) throws ApplicationNotFoundException {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(userId,id);
        if(jobApplication == null){
            throw new ApplicationNotFoundException("Application not found");
        }
        jobApplication.setStatus(request.getStatus());
        JobApplication savedApp = jobApplicationRepository.save(jobApplication);

        ApplicationStatusEvent event = new ApplicationStatusEvent(
            savedApp.getId(),
            savedApp.getUserId(),
            savedApp.getCompanyName(),
            savedApp.getJobTitle(),
            savedApp.getStatus(),
            ""
        );
        kafkaProducerService.publishStatusChangeEvent(event);

        JobApplicationResponse jobAppResponse = new JobApplicationResponse();
        jobAppResponse.setStatus(savedApp.getStatus());
        jobAppResponse.setId(savedApp.getId());
        jobAppResponse.setCompanyName(savedApp.getCompanyName());
        jobAppResponse.setJobTitle(savedApp.getJobTitle());
        jobAppResponse.setUserId(savedApp.getUserId());
        return jobAppResponse;
    }

    @Transactional
    @Override
    public void deleteApplication(Long id, Long userId) throws IllegalAccessException {
        JobApplication jobApp = jobApplicationRepository.findById(id).orElseThrow(()-> new ApplicationNotFoundException("Application not found!"));

        if(!jobApp.getUserId().equals(userId)){
            throw new IllegalAccessException("You do not have permission to delete this application. ");
        }else {
            jobApplicationRepository.deleteById(id);
        }


    }
}

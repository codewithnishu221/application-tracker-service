package application.tracker.service.service.Impl;

import application.tracker.service.client.MatchServiceClient;
import application.tracker.service.dto.JobApplicationRequest;
import application.tracker.service.dto.JobApplicationResponse;
import application.tracker.service.dto.MatchScoreResponse;
import application.tracker.service.dto.ReuseRecommendation;
import application.tracker.service.dto.UpdateStatusRequest;
import application.tracker.service.entity.JobApplication;
import application.tracker.service.enums.ApplicationStatus;
import application.tracker.service.events.ApplicationStatusEvent;
import application.tracker.service.exceptions.ApplicationNotFoundException;
import application.tracker.service.exceptions.UnauthorizedAccessException;
import application.tracker.service.repository.JobApplicationRepository;
import application.tracker.service.service.EmbeddingWorkerService;
import application.tracker.service.service.JobApplicationService;
import application.tracker.service.service.KafkaProducerService;
import application.tracker.service.util.EmbeddingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final MatchServiceClient matchServiceClient;
    private final KafkaProducerService kafkaProducerService;

    private final OllamaEmbeddingModel embeddingModel;
    private final EmbeddingWorkerService embeddingWorkerService;
    private final EmbeddingUtils embeddingUtils;
    
    @Value("${app.similarity.threshold}")
    private double scoreThreshold;


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
            embeddingWorkerService.generateAndSaveEmbedding(savedApp.getId(), request.getJobDescription());

        }
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(savedApp.getId());
        response.setCompanyName(savedApp.getCompanyName());
        response.setAppliedAt(savedApp.getAppliedAt());
        response.setJobTitle(savedApp.getJobTitle());
        response.setUserId(savedApp.getUserId());
        response.setStatus(savedApp.getStatus());
        response.setResumeId(savedApp.getResumeId());
        response.setMatchScore(savedApp.getMatchScore());
        response.setDescription(savedApp.getJobDescription());
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
    public JobApplicationResponse getApplicationById(Long id, Long userId) {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(userId,id);
        if(jobApplication == null){
                    throw new ApplicationNotFoundException("Application not found for id: " + id);
        }
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
    public JobApplicationResponse updateStatus(Long id, Long userId, UpdateStatusRequest request) {
        JobApplication jobApplication = jobApplicationRepository.findByUserIdAndId(userId,id);
        if(jobApplication == null){
                   throw new ApplicationNotFoundException("Application not found for id: " + id);
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
    public void deleteApplication(Long id, Long userId) {
        JobApplication jobApp = jobApplicationRepository.findById(id).orElseThrow(()-> new ApplicationNotFoundException("Application not found!"));

        if(!jobApp.getUserId().equals(userId)){
            throw new UnauthorizedAccessException("You do not have permission to delete this application. ");
        }else {
            jobApplicationRepository.deleteById(id);
        }


    }

    @Override
    public ReuseRecommendation checkReuseRecommendation(String newJdText, Long userId) {
        float[] jdEmbedding = embeddingModel.embed(newJdText);

        List<JobApplication> jobWithEmbeddings = jobApplicationRepository.findByUserIdAndJdEmbeddingIsNotNull(userId);

        double highestScore = 0.0;
        JobApplication bestMatch = null;

        for(JobApplication job: jobWithEmbeddings){
            double similarityScore = embeddingUtils.cosineSimilarity(jdEmbedding, job.getJdEmbedding()) * 100;
            if(similarityScore > highestScore){
                highestScore = similarityScore;
                bestMatch = job;
            }
        }

        ReuseRecommendation recommendation = new ReuseRecommendation();
        if(bestMatch != null && highestScore > scoreThreshold){
            recommendation.setShouldReuse(true);
            recommendation.setSimilarityScore(highestScore);
            recommendation.setRecommendedResumeId(bestMatch.getResumeId());
            recommendation.setMatchedJobTitle(bestMatch.getJobTitle());
            recommendation.setMatchedCompanyName(bestMatch.getCompanyName());
            recommendation.setMessage("Your resume for " + bestMatch.getJobTitle()
                    + " at " + bestMatch.getCompanyName()
                    + " is " + Math.round(highestScore) + "% similar — consider reusing it");
        } else {
            recommendation.setShouldReuse(false);
            recommendation.setSimilarityScore(highestScore);
            recommendation.setMessage("No similar past applications found — a fresh resume is recommended");
        }
        return recommendation;
    }

    @Override
    public void generateAndSaveEmbedding(Long applicationId, String jobDescription) {
        embeddingWorkerService.generateAndSaveEmbedding(applicationId, jobDescription);
    }


}

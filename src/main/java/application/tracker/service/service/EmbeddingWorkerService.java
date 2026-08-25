package application.tracker.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import application.tracker.service.entity.JobApplication;
import application.tracker.service.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingWorkerService {
 
    private final JobApplicationRepository jobApplicationRepository;
    private final OllamaEmbeddingModel ollamaEmbeddingModel;

    @Async
    public void generateAndSaveEmbedding(Long applicationId, String jobDescription) {
        try{
      if(jobDescription ==null || jobDescription.isEmpty()){
          return;
      }
      float[] embedding = ollamaEmbeddingModel.embed(jobDescription);
      if(embedding != null && embedding.length > 0){
          String vectorString = Arrays.toString(embedding);
          jobApplicationRepository.updateJdEmbedding(applicationId, vectorString);
          log.info("Saved embedding for applicationId: {}", applicationId);
      }
//      JobApplication job = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new RuntimeException("Job not found"));
//        job.setJdEmbedding(embedding);
//      jobApplicationRepository.save(job);
    } catch (Exception e) {
      System.err.println("Warning: Failed to generate embedding due to Ollama downtime: " + e.getMessage());
    }
} 
}

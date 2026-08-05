package application.tracker.service.service;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import application.tracker.service.entity.JobApplication;
import application.tracker.service.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
      JobApplication job = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setJdEmbedding(embedding);
      jobApplicationRepository.save(job);
    } catch (Exception e) {
      System.err.println("Warning: Failed to generate embedding due to Ollama downtime: " + e.getMessage());
    }
} 
}

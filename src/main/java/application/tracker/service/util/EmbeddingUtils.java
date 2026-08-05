package application.tracker.service.util;

import org.springframework.stereotype.Component;

@Component
public class EmbeddingUtils {
    public double cosineSimilarity(float[] vectorA, float[] vectorB){
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;
        for(int i=0; i<vectorA.length; i++){
            dotProduct += vectorA[i] * vectorB[i];
            magnitudeA += vectorA[i] * vectorA[i];
            magnitudeB += vectorB[i] * vectorB[i];
        }
        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);
        if( magnitudeA == 0 || magnitudeB ==0){ 
            return 0.0;
        } else{
            return dotProduct /(magnitudeA * magnitudeB);
        }

}

}
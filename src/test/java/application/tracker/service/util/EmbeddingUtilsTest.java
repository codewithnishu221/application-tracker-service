package application.tracker.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmbeddingUtilsTest {

    private final EmbeddingUtils embeddingUtils = new EmbeddingUtils();

    @Test
    @DisplayName("Identical vectors return similarity of 1.0")
    void cosineSimilarity_ReturnsOneForIdenticalVectors(){
        float[] vectorA = {1.0f, 2.0f,3.0f};
        float[] vectorB = {1.0f, 2.0f, 3,0f};
         double result = embeddingUtils.cosineSimilarity(vectorA, vectorB);

         assertEquals(1.0, result, 0.0001);

    }

    @Test
    @DisplayName("Zero vector returns similarity of 0.0")
    void cosineSimilarity_ReturnsZero_WhenOneVectorIsZero(){
        float[] vectorA = {0.0f, 0.0f, 0.0f};
        float[] vectorB = {1.0f, 2.0f, 3.0f};

        double result = embeddingUtils.cosineSimilarity(vectorA, vectorB);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    @DisplayName("Perpendicular vectors return similarity of 0.0")
    void consineSimilarity_ReturnsZero_ForPerpendicularVectors(){
        float[] vectorA = {1.0f, 0.0f, 0.0f};
        float[] vectorB = {0.0f, 1.0f, 0.0f};
        double result = embeddingUtils.cosineSimilarity(vectorA, vectorB);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    @DisplayName("Similar vectors return high similarity score")
    void cosineSimilarity_ReturnHighScore_ForSimilarVectors(){
        float[] vectorA = {1.0f, 1.0f, 1.0f};
        float[] vectorB = {1.0f, 1.0f, 0.9f};
        double result = embeddingUtils.cosineSimilarity(vectorA, vectorB);

        assertTrue(result > 0.99);
        assertTrue(result <= 1.0);
    }
}

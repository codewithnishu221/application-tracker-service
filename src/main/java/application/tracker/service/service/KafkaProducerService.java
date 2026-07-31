package application.tracker.service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import application.tracker.service.events.ApplicationStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, ApplicationStatusEvent> kafkaTemplate;
    public static final String TOPIC = "application-status-events";

    public void publishStatusChangeEvent(ApplicationStatusEvent event){
        try{
            kafkaTemplate.send(TOPIC, String.valueOf(event.getApplicationId()), event);
            log.info("Published status event for applicationId: {}", event.getApplicationId());

        } catch (Exception e){
            log.error("Failed to publish Kafka event for applicationId: {}",event.getApplicationId(), e);
        }
    }

}

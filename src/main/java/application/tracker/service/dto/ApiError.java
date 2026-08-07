package application.tracker.service.dto;

import java.time.LocalDateTime;

import org.apache.kafka.common.protocol.types.Field.Str;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

}

package application.tracker.service.dto;

import application.tracker.service.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest {

    private ApplicationStatus status;
    private  LocalDate interviewDate;
}

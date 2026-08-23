package application.tracker.service.events;

import application.tracker.service.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusEvent {
    private Long applicationId;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private ApplicationStatus newStatus;
    private String userEmail;
    private String userName;
}
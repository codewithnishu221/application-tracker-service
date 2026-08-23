package application.tracker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingInterviewDto {

    private Long applicationId;
    private Long userId;
    private String userEmail;
    private String userName;
    private String companyName;
    private String jobTitle;
}

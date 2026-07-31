package application.tracker.service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationRequest {

    @NotBlank
    private String companyName;
    @NotBlank
    private String jobTitle;
    @NotBlank
    private String jobDescription;

    private Long resumeId;


}

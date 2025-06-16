package graduation_project.Dailic.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentLicenseDto {
    private String occupation;
    private String license;
    private int totalQuestion;
    private int solvedQuestion;
}

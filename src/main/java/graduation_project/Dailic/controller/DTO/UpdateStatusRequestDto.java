package graduation_project.Dailic.controller.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequestDto {
    private Long userId;
    private Long problemId;
    private Boolean isCorrect;
    private String userAnswer;
    private Boolean isRetried;
}

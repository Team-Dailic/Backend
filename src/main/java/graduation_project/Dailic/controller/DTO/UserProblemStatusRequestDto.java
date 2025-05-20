package graduation_project.Dailic.controller.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProblemStatusRequestDto {
    private Long userId;
    private Long problemId;
    private String userAnswer;
    private Boolean isRetried;
}

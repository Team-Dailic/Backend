package graduation_project.Dailic.controller.DTO;

import graduation_project.Dailic.domain.UserProblemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProblemStatusResponseDto {
    private Long id;
    private Long userId;
    private Long problemId;
    private String questionText;
    private Boolean isCorrect;
    private Boolean isScraped;
    private String userAnswer;
    private Boolean isRetried;
    private LocalDateTime answeredAt;

    public static UserProblemStatusResponseDto fromEntity(UserProblemStatus status) {
        UserProblemStatusResponseDto dto = new UserProblemStatusResponseDto();
        dto.setId(status.getId());
        dto.setUserId(status.getUser().getId());
        dto.setProblemId(status.getProblem().getId());
        dto.setQuestionText(status.getProblem().getQuestionText());
        dto.setIsCorrect(status.getIsCorrect());
        dto.setIsScraped(status.getIsScraped());
        dto.setUserAnswer(status.getUserAnswer());
        dto.setIsRetried(status.getIsRetried());
        dto.setAnsweredAt(status.getAnsweredAt());
        return dto;
    }
}
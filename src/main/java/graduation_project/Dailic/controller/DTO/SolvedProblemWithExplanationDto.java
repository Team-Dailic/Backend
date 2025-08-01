package graduation_project.Dailic.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SolvedProblemWithExplanationDto {
    private Long id;
    private String questionText;
    private List<String> options;
    private int correctAnswer;
    private int userAnswer;
    private boolean isCorrect;
    private String explanation;
}

package graduation_project.Dailic.controller.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProblemCreateRequest (
    @NotBlank String questionText,
    @NotBlank String option1,
    @NotBlank String option2,
    @NotBlank String option3,
    @NotBlank String option4,
    String option5, //5지선다 아니면 null 허용
    @Min(1) @Max(5) Integer correctAnswer, // 1~5번
    String solution // 해설 없으면 null
) {}

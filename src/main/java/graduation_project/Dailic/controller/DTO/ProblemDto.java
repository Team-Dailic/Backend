package graduation_project.Dailic.controller.DTO;

import graduation_project.Dailic.domain.Problem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProblemDto {
    private Long id;
    private String questionText; // 문제
    private List<String> options; //객관식 보기
    private String correctAnswer; //정답 - withSolution=true 일 때만 포함
    private String solution; //문제 해설 - withSolution=true 일 때만 포함

    //생성자는 외부에서 직접 사용하지 않도록 private
    private ProblemDto(Long id, String questionText, List<String> options, String correctAnswer, String solution) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.solution = solution;
    }

    //Problem 엔티티 객체를 받아서 -> 프론트에서 보기 좋은 형태의 DTO로 변환
    public static ProblemDto from(Problem problem, boolean withSolution) {
        List<String> options = new ArrayList<>();
        // 보기 항목 중 null이 아닌 것만 리스트에 담음 (선택지 개수가 유동적일 경우 대비)
        if (problem.getOption1() != null) options.add(problem.getOption1());
        if (problem.getOption2() != null) options.add(problem.getOption2());
        if (problem.getOption3() != null) options.add(problem.getOption3());
        if (problem.getOption4() != null) options.add(problem.getOption4());
        if (problem.getOption5() != null) options.add(problem.getOption5());

        return new ProblemDto(
                problem.getId(),
                problem.getQuestionText(),
                options,
                withSolution ? problem.getCorrectAnswer() : null,
                withSolution ? problem.getSolution() : null
        );
    }
}



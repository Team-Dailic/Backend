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
    private boolean isScraped; // 스크랩 여부
    private String correctAnswer; //정답 - withSolution=true 일 때만 포함
    private String solution; //문제 해설 - withSolution=true 일 때만 포함

    //  isScraped 매개변수 추가
    private ProblemDto(Long id, String questionText, List<String> options, boolean isScraped, String correctAnswer, String solution) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.isScraped = isScraped; //  필드 할당
        this.correctAnswer = correctAnswer;
        this.solution = solution;
    }

    // isScraped 매개변수 추가
    public static ProblemDto from(Problem problem, boolean withSolution, boolean isScraped) {
        List<String> options = new ArrayList<>();
        if (problem.getOption1() != null) options.add(problem.getOption1());
        if (problem.getOption2() != null) options.add(problem.getOption2());
        if (problem.getOption3() != null) options.add(problem.getOption3());
        if (problem.getOption4() != null) options.add(problem.getOption4());
        if (problem.getOption5() != null) options.add(problem.getOption5());

        // 문제 엔티티의 correctAnswer가 String 타입이므로, "1", "2" 등을 option1, option2 형식으로 변환해야 할 수 있습니다.
        // 현재 로직을 유지하면서, 문제 엔티티의 correctAnswer 필드가 String이므로, Integer.parseInt(p.getCorrectAnswer()) 대신 그대로 사용할 경우를 대비하여 아래 로직을 사용합니다.
        // 문제 엔티티의 correctAnswer가 String이므로 "option" + problem.getCorrectAnswer()로 변환하는 것이 맞는지 확인 필요. (현재는 String 타입이므로 Integer.parseInt 로직이 없는 ProblemDto에서는 이 로직이 맞을 수 있습니다.)
        String correct = withSolution ? "option" + problem.getCorrectAnswer() : null;


        return new ProblemDto(
                problem.getId(),
                problem.getQuestionText(),
                options,
                isScraped, //  isScraped 전달
                correct,
                withSolution ? problem.getSolution() : null
        );
    }
}



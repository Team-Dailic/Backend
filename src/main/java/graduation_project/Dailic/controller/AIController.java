package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ApiResponse;
import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.service.AIService;
import graduation_project.Dailic.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final ProblemService problemService; // 🔥 여기에 추가

    /**
     * 자유 질문 API
     */
    @PostMapping("/ask/{problemId}") // URL 및 PathVariable 설정
    public ResponseEntity<ApiResponse<String>> ask(
            @PathVariable Long problemId,
            @RequestParam Long userId,
            @RequestBody AskRequest request
    ) {
        // 운전면허 자격증으로 한정 (문맥 설정용)
        String licenseName = "운전면허 시험";

        // AIService의 ask 메서드를 문맥 기반 로직으로 호출
        String aiResponse = aiService.ask(problemId, userId, licenseName, request.getQuestion());

        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "AI 문맥 기반 답변 조회가 완료되었습니다.",
                aiResponse
        );
        return ResponseEntity.ok(response);
    }

    //--- 문제 해설 API ---
    @PostMapping("/explain/{problemId}")
    // 반환 타입 변경
    public ResponseEntity<ApiResponse<String>> explain(
            @PathVariable Long problemId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeSolution
    ) {
        ProblemDto dto = problemService.getProblemDtoById(problemId, includeSolution, userId);
        String aiExplanation = aiService.explain(dto, includeSolution);

        // ApiResponse로 감싸서 반환
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "AI 해설 조회가 완료되었습니다.",
                aiExplanation
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 DTO: AskRequest
     */
    public static class AskRequest {
        private String question;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }
}

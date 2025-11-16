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
    @PostMapping("/ask")
    // 반환 타입 변경
    public ResponseEntity<ApiResponse<String>> ask(@RequestBody AskRequest request) {
        String aiResponse = aiService.ask(request.getQuestion());

        // ApiResponse로 감싸서 반환
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "AI 답변 조회가 완료되었습니다.",
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

package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.service.AIService;
import graduation_project.Dailic.service.ProblemService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> ask(@RequestBody AskRequest request) {
        return ResponseEntity.ok(aiService.ask(request.getQuestion()));
    }

    // 🔹 문제 해설 API
    // GET/POST: /ai/explain/{problemId}?userId={userId}&includeSolution=true
    @PostMapping("/explain/{problemId}")
    public ResponseEntity<String> explain(
            @PathVariable Long problemId,
            // ✨ 1. userId를 쿼리 파라미터로 추가
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeSolution
    ) {
        // ✨ 2. problemService 호출 시 userId 전달
        ProblemDto dto = problemService.getProblemDtoById(problemId, includeSolution, userId);
        return ResponseEntity.ok(aiService.explain(dto, includeSolution));
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

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

    /**
     * 🔹 문제 해설 API
     * GET/POST: /ai/explain/{problemId}?includeSolution=true
     * includeSolution=true면 정답+기존 해설 포함
     */
    @PostMapping("/explain/{problemId}")
    public ResponseEntity<String> explain(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false") boolean includeSolution
    ) {
        ProblemDto dto = problemService.getProblemDtoById(problemId, includeSolution);
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

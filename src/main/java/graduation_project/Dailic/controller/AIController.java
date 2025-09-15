package graduation_project.Dailic.controller;

import graduation_project.Dailic.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    /**
     * 자유 질문 API
     */
    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String question) {
        String answer = aiService.ask(question);
        return ResponseEntity.ok(answer);
    }

    /**
     * 문제 해설 API

    @PostMapping("/explain/{problemId}")
    public ResponseEntity<String> explain(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false") boolean includeSolution
    ) {
        ProblemDto dto = problemService.getProblemDtoById(problemId, includeSolution);
        return ResponseEntity.ok(aiService.explain(dto, includeSolution));
    }
            */
}

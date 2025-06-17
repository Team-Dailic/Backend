package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    //문제 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getProblemById(
            @PathVariable Long id,
            @RequestParam(value = "withSolution", defaultValue = "false")
            boolean withSolution) {
        ProblemDto problemDto = problemService.getProblemDtoById(id, withSolution);
        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "문제 단건 조회 성공,",
                        "data", problemDto
                )
        );
    }
}

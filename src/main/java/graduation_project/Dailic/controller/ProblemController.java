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
            // ✨ userId 추가
            @RequestParam Long userId,
            @RequestParam(value = "withSolution", defaultValue = "false")
            boolean withSolution) {

        // ProblemService의 메서드를 userId를 받도록 수정하고 호출
        // (ProblemService에서 User 객체와 UserProblemStatus를 조회하는 로직이 필요)
        ProblemDto problemDto = problemService.getProblemDtoById(id, withSolution, userId);

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "문제 단건 조회 성공,",
                        "data", problemDto
                )
        );
    }
}

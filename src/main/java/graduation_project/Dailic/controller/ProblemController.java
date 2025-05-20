package graduation_project.Dailic.controller;

import graduation_project.Dailic.DTO.ProblemDto;
import graduation_project.Dailic.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    //문제 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProblemDto> getProblemById(
            @PathVariable Long id,
            @RequestParam(value = "withSolution", defaultValue = "false")
            boolean whithSolution) {
        ProblemDto problemDto = problemService.getProblemDtoById(id, whithSolution);
        return ResponseEntity.ok(problemDto);
    }
}

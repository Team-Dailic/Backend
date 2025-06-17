package graduation_project.Dailic.controller;


import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.domain.DailyProblem;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.service.DailyProblemService;
import graduation_project.Dailic.service.ProblemService;
import graduation_project.Dailic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily-problems")
public class DailyProblemController {
    private final DailyProblemService dailyProblemService;
    private final ProblemService problemService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> generateDailyProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        // 이미 오늘 문제가 생성됐는지 확인
        List<DailyProblem> existing = dailyProblemService.getDailyProblemsForUser(user, today);
        if (!existing.isEmpty()) {
            return ResponseEntity.ok("이미 오늘의 문제가 생성되어 있습니다.");
        }

        // ⛳ 진짜 Problem 엔티티 20개 가져옴 (이게 핵심)
        List<Problem> randomProblems = problemService.findRandomProblemEntities(20);

        for (int i = 0; i < randomProblems.size(); i++) {
            DailyProblem dp = new DailyProblem();
            dp.setUser(user);
            dp.setProblem(randomProblems.get(i));
            dp.setDate(today);
            dp.setSequenceNumber(i + 1);
            dailyProblemService.saveDailyProblem(dp);
        }

        return ResponseEntity.ok("오늘의 문제 20개가 생성되었습니다.");
    }

    // ✅ 오늘의 문제 조회
    @GetMapping
    public ResponseEntity<List<ProblemDto>> getTodayProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        List<DailyProblem> dailyProblems = dailyProblemService.getDailyProblemsForUser(user, today);

        List<ProblemDto> result = dailyProblems.stream()
                .map(dp -> ProblemDto.from(dp.getProblem(), false)) // 해설 없이 반환
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ 추가 문제 요청 (옵션)
    @PostMapping("/more")
    public ResponseEntity<?> addMoreProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        List<DailyProblem> current = dailyProblemService.getDailyProblemsForUser(user, today);

        List<Long> alreadyUsedIds = current.stream()
                .map(dp -> dp.getProblem().getId())
                .collect(Collectors.toList());

        List<Problem> newProblems = problemService.findRandomProblemEntities(30).stream()
                .filter(p -> !alreadyUsedIds.contains(p.getId()))
                .limit(5)
                .collect(Collectors.toList());

        int startSeq = current.size();

        for (int i = 0; i < newProblems.size(); i++) {
            DailyProblem dp = new DailyProblem();
            dp.setUser(user);
            dp.setProblem(newProblems.get(i));
            dp.setDate(today);
            dp.setSequenceNumber(startSeq + i + 1);
            dailyProblemService.saveDailyProblem(dp);
        }

        return ResponseEntity.ok("추가 문제 5개 생성 완료");
    }

    @GetMapping("/explanations")
    public ResponseEntity<?> getTodayDailyProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        //오늘의 문제 가져오기
        List<DailyProblem> dailyProblems = dailyProblemService.getDailyProblemsForUser(user, today);
        if (dailyProblems.isEmpty()) {
            return ResponseEntity.status(409).body(
                    Map.of(
                            "status", 409,
                            "message", "오늘의 문제가 존재하지 않습니다.",
                            "data", null
                    )
            );
        }

        //문제들을 DTO로 변환 (해설 포함)
        List<ProblemDto> result = dailyProblems.stream()
                .map(dp -> ProblemDto.from(dp.getProblem(), true))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "오늘의 문제 해설 조회 성공",
                        "data", result
                )
        );
    }

}
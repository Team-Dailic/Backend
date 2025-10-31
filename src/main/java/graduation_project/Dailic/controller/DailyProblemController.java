package graduation_project.Dailic.controller;


import graduation_project.Dailic.controller.DTO.ApiResponse;
import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.controller.DTO.SolvedProblemWithExplanationDto;
import graduation_project.Dailic.domain.*;
import graduation_project.Dailic.repository.UserProblemStatusRepository;
import graduation_project.Dailic.service.DailyProblemService;
import graduation_project.Dailic.service.LicenseService;
import graduation_project.Dailic.service.ProblemService;
import graduation_project.Dailic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final UserProblemStatusRepository userProblemStatusRepository;
    private final LicenseService licenseService;

    @PostMapping
    public ResponseEntity<?> generateDailyProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        LicenseSelection selection;
        try {
            selection = licenseService.getCurrentLicenseSelection(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }

        // 이미 오늘 문제가 생성됐는지 확인
        List<DailyProblem> existing = dailyProblemService.getDailyProblemsForUser(user, today);
        if (!existing.isEmpty()) {
            return ResponseEntity.ok("이미 오늘의 문제가 생성되어 있습니다.");
        }

        List<Problem> randomProblems = problemService.findRandomProblemEntitiesByLicense(selection.getLicense(), 20);

        if (randomProblems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "선택한 자격증에 해당하는 문제가 없습니다.", null));
        }

        for (int i = 0; i < randomProblems.size(); i++) {
            DailyProblem dp = new DailyProblem();
            dp.setUser(user);
            dp.setProblem(randomProblems.get(i));
            dp.setDate(today);
            dp.setSequenceNumber(i + 1);
            dailyProblemService.saveDailyProblem(dp);
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "오늘의 문제 20개가 생성되었습니다.", null));
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

        // 사용자의 현재 선택 자격증을 가져옵니다.
        LicenseSelection selection;
        try {
            selection = licenseService.getCurrentLicenseSelection(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }

        List<DailyProblem> current = dailyProblemService.getDailyProblemsForUser(user, today);

        List<Long> alreadyUsedIds = current.stream()
                .map(dp -> dp.getProblem().getId())
                .collect(Collectors.toList());

        // '선택한 자격증'의 문제 30개를 가져와서 필터링합니다.
        List<Problem> newProblems = problemService.findRandomProblemEntitiesByLicense(selection.getLicense(), 30).stream()
                .filter(p -> !alreadyUsedIds.contains(p.getId()))
                .limit(5)
                .collect(Collectors.toList());

        if (newProblems.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(200, "추가할 수 있는 새로운 문제가 없습니다.", null));
        }

        int startSeq = current.size();

        for (int i = 0; i < newProblems.size(); i++) {
            DailyProblem dp = new DailyProblem();
            dp.setUser(user);
            dp.setProblem(newProblems.get(i));
            dp.setDate(today);
            dp.setSequenceNumber(startSeq + i + 1);
            dailyProblemService.saveDailyProblem(dp);
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "추가 문제 " + newProblems.size() + "개 생성 완료", null));
    }

    @GetMapping("/explanations")
    public ResponseEntity<ApiResponse<List<SolvedProblemWithExplanationDto>>> getTodayDailyProblems(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();

        //오늘의 문제 가져오기
        List<DailyProblem> dailyProblems = dailyProblemService.getDailyProblemsForUser(user, today);
        if (dailyProblems.isEmpty()) {
            return ResponseEntity.status(409).body(
                    new ApiResponse<>(409,
                            "오늘의 문제가 존재하지 않습니다.",
                            null)
            );
        }

        //문제들을 DTO로 변환 (해설 포함)
        List<SolvedProblemWithExplanationDto> result = dailyProblems.stream().map(dp -> {
            Problem p = dp.getProblem();
            UserProblemStatus status = userProblemStatusRepository
                    .findByUserAndProblem(user, p)
                    .orElse(null);

            // 보기 리스트 구성
            List<String> options = new ArrayList<>();
            if (p.getOption1() != null && !p.getOption1().trim().isEmpty()) options.add(p.getOption1());
            if (p.getOption2() != null && !p.getOption2().trim().isEmpty()) options.add(p.getOption2());
            if (p.getOption3() != null && !p.getOption3().trim().isEmpty()) options.add(p.getOption3());
            if (p.getOption4() != null && !p.getOption4().trim().isEmpty()) options.add(p.getOption4());
            if (p.getOption5() != null && !p.getOption5().trim().isEmpty()) options.add(p.getOption5());

            int correctAnswer = Integer.parseInt(p.getCorrectAnswer());

            int userAnswer = -1;
            if (status != null && status.getUserAnswer() != null) {
                try {
                    userAnswer = Integer.parseInt(status.getUserAnswer());
                } catch (NumberFormatException e) {
                    userAnswer = -1;
                }
            }

            boolean isCorrect = (status != null && Boolean.TRUE.equals(status.getIsCorrect()));

            return new SolvedProblemWithExplanationDto(
                    p.getId(),
                    p.getQuestionText(),
                    options,
                    correctAnswer,
                    userAnswer,
                    isCorrect,
                    p.getSolution()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(200, "오늘의 문제 해설 조회 성공", result)
        );
    }

}
package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.*;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.UserProblemStatus;
import graduation_project.Dailic.service.LicenseService;
import graduation_project.Dailic.service.ProblemService;
import graduation_project.Dailic.service.UserProblemStatusService;
import graduation_project.Dailic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-problem-status")
@RequiredArgsConstructor
public class UserProblemStatusController {

    private final UserService userService;
    private final ProblemService problemService;
    private final UserProblemStatusService userProblemStatusService;
    private final LicenseService licenseService;

    // 정답 제출 및 채점
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<UserProblemStatusResponseDto>> submitAnswer(@RequestBody UserProblemStatusRequestDto requestDto) {

        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "유저를 찾을 수 없습니다.", null));
        }

        Optional<Problem> problemOpt = problemService.findById(requestDto.getProblemId());
        if (problemOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "문제를 찾을 수 없습니다.", null));
        }
        Problem problem = problemOpt.get();

        Boolean isCorrect = problem.getCorrectAnswer().equals(requestDto.getUserAnswer());

        UserProblemStatus status = userProblemStatusService.getUserProblemStatus(user, problem)
                .orElseGet(() -> new UserProblemStatus(user, problem, isCorrect, false, requestDto.getUserAnswer(), false, LocalDateTime.now()));

        // 기존에 존재할 경우 업데이트
        if (status.getId() != null) {
            status.setIsCorrect(isCorrect);
            status.setUserAnswer(requestDto.getUserAnswer());
            status.setIsRetried(requestDto.getIsRetried());
            status.setAnsweredAt(LocalDateTime.now());
        }

        UserProblemStatus saved = userProblemStatusService.saveUserProblemStatus(status);
        UserProblemStatusResponseDto responseDto = UserProblemStatusResponseDto.fromEntity(saved);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "정답이 성공적으로 제출되었습니다.", responseDto));
    }

    // 오답노트 문제 전체 조회
    @GetMapping("/wrong/{userId}")
    public ResponseEntity<ApiResponse<List<UserProblemStatusResponseDto>>> getWrongProblems(@PathVariable Long userId) {
        User user;
        LicenseSelection selection;
        try{
            user = userService.getUserById(userId);
            selection = licenseService.getCurrentLicenseSelection(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }

        List<UserProblemStatus> wrongList = userProblemStatusService.getIncorrectProblems(user, selection.getLicense());

        List<UserProblemStatusResponseDto> result = wrongList.stream()
                .map(UserProblemStatusResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "오답 노트 조회가 완료되었습니다.", result));
    }

    // 문제 스크랩 등록 및 취소
    @PutMapping("/scrap")
    public ResponseEntity<ApiResponse<UserProblemStatusResponseDto>> updateScrapStatus(@RequestBody ScrapUpdateRequestDto requestDto) {
        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "유저를 찾을 수 없습니다.", null));
        }

        Problem problem = problemService.findById(requestDto.getProblemId())
                .orElse(null);
        if (problem == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "문제를 찾을 수 없습니다.", null));
        }

        // 핵심 수정: 상태가 존재하지 않으면 새로운 객체를 Builder를 사용하여 생성
        UserProblemStatus status = userProblemStatusService.getUserProblemStatus(user, problem)
                .orElseGet(() -> {
                    // @Column(nullable=false) 제약 조건을 만족하는 기본값 설정
                    return UserProblemStatus.builder()
                            .user(user)
                            .problem(problem)
                            // 풀지 않았으므로, isCorrect는 false 또는 Nullable을 허용해야 하지만,
                            // 현재 @Column(nullable = false) 이므로 기본값 false로 설정합니다.
                            .isCorrect(false)
                            // 스크랩 여부는 초기값 false로 설정 (아래에서 요청 값으로 덮어쓰기 예정)
                            .isScraped(false)
                            .userAnswer(null)      // 답안 없음
                            .isRetried(false)      // 재풀이 아님
                            // LocalDateTime 필드 역시 nullable=false 이므로, 현재 시간으로 기록
                            .answeredAt(LocalDateTime.now())
                            .build();
                });

        // 요청받은 스크랩 상태를 설정 (기존 레코드 또는 새로 생성된 레코드 모두 적용)
        status.setIsScraped(requestDto.getIsScraped());

        // 저장 (기존 레코드 업데이트 또는 새 레코드 INSERT)
        UserProblemStatus updated = userProblemStatusService.saveUserProblemStatus(status);
        UserProblemStatusResponseDto responseDto = UserProblemStatusResponseDto.fromEntity(updated);
        String message = requestDto.getIsScraped() ? "문제가 스크랩되었습니다." : "문제 스크랩이 취소되었습니다.";

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), message, responseDto));
    }

    // 스크랩 문제 전체 조회
    @GetMapping("/scrap/{userId}")
    public ResponseEntity<ApiResponse<List<UserProblemStatusResponseDto>>> getScrapedProblems(@PathVariable Long userId) {
        User user;
        LicenseSelection selection;
        try{
            user = userService.getUserById(userId);
            // [수정] 현재 선택한 자격증 정보 조회
            selection = licenseService.getCurrentLicenseSelection(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }

        // [수정] 자격증 기준으로 스크랩 목록 조회
        List<UserProblemStatus> scrapList = userProblemStatusService.getScrapedProblems(user, selection.getLicense());

        List<UserProblemStatusResponseDto> result = scrapList.stream()
                .map(UserProblemStatusResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "스크랩 문제 조회가 완료되었습니다.", result));
    }

    // 문제 풀이 상태 업데이트 (정답 여부, 사용자 답안, 재풀이 여부)
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserProblemStatusResponseDto>> updateStatus(@RequestBody UpdateStatusRequestDto requestDto) {
        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "유저를 찾을 수 없습니다.", null));
        }

        Problem problem = problemService.findById(requestDto.getProblemId())
                .orElse(null);
        if (problem == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "문제를 찾을 수 없습니다.", null));
        }

        UserProblemStatus existing = userProblemStatusService.getUserProblemStatus(user, problem)
                .orElse(null);
        if (existing == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "문제 풀이 상태가 존재하지 않습니다.", null));
        }

        UserProblemStatus updated = userProblemStatusService.updateUserProblemStatus(
                existing,
                requestDto.getIsCorrect(),
                requestDto.getUserAnswer(),
                requestDto.getIsRetried()
        );
        UserProblemStatusResponseDto responseDto = UserProblemStatusResponseDto.fromEntity(updated);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "문제 풀이 상태가 성공적으로 업데이트되었습니다.", responseDto));
    }
}

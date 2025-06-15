package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ScrapUpdateRequestDto;
import graduation_project.Dailic.controller.DTO.UpdateStatusRequestDto;
import graduation_project.Dailic.controller.DTO.UserProblemStatusRequestDto;
import graduation_project.Dailic.controller.DTO.UserProblemStatusResponseDto;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.UserProblemStatus;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-problem-status")
@RequiredArgsConstructor
public class UserProblemStatusController {

    private final UserService userService;
    private final ProblemService problemService;
    private final UserProblemStatusService userProblemStatusService;

    // 정답 제출 및 채점
    @PostMapping("/submit")
    public ResponseEntity<UserProblemStatusResponseDto> submitAnswer(@RequestBody UserProblemStatusRequestDto requestDto) {

        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다");
        }

        Problem problem = problemService.findById(requestDto.getProblemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다"));

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

        return ResponseEntity.ok(UserProblemStatusResponseDto.fromEntity(saved));
    }

    // 오답노트 문제 전체 조회
    @GetMapping("/wrong/{userId}")
    public ResponseEntity<List<UserProblemStatusResponseDto>> getWrongProblems(@PathVariable Long userId) {
        User user;
        try{
           user = userService.getUserById(userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        List<UserProblemStatus> wrongList = userProblemStatusService.getIncorrectProblems(user);
        List<UserProblemStatusResponseDto> result = wrongList.stream()
                .map(UserProblemStatusResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 문제 스크랩 등록 및 취소
    @PutMapping("/scrap")
    public ResponseEntity<UserProblemStatusResponseDto> updateScrapStatus(@RequestBody ScrapUpdateRequestDto requestDto) {
        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다");
        }

        Problem problem = problemService.findById(requestDto.getProblemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다"));

        UserProblemStatus status = userProblemStatusService.getUserProblemStatus(user, problem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제 풀이 상태가 존재하지 않습니다"));

        status.setIsScraped(requestDto.getIsScraped());
        UserProblemStatus updated = userProblemStatusService.saveUserProblemStatus(status);

        return ResponseEntity.ok(UserProblemStatusResponseDto.fromEntity(updated));
    }

    // 스크랩 문제 전체 조회
    @GetMapping("/scrap/{userId}")
    public ResponseEntity<List<UserProblemStatusResponseDto>> getScrapedProblems(@PathVariable Long userId) {
        User user;
        try{
            user = userService.getUserById(userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        List<UserProblemStatus> scrapList = userProblemStatusService.getScrapedProblems(user);
        List<UserProblemStatusResponseDto> result = scrapList.stream()
                .map(UserProblemStatusResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 문제 풀이 상태 업데이트 (정답 여부, 사용자 답안, 재풀이 여부)
    @PutMapping("/update")
    public ResponseEntity<UserProblemStatusResponseDto> updateStatus(@RequestBody UpdateStatusRequestDto requestDto) {
        User user;
        try {
            user = userService.getUserById(requestDto.getUserId());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다");
        }

        Problem problem = problemService.findById(requestDto.getProblemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다"));

        UserProblemStatus existing = userProblemStatusService.getUserProblemStatus(user, problem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문제 풀이 상태가 존재하지 않습니다"));

        UserProblemStatus updated = userProblemStatusService.updateUserProblemStatus(
                existing,
                requestDto.getIsCorrect(),
                requestDto.getUserAnswer(),
                requestDto.getIsRetried()
        );

        return ResponseEntity.ok(UserProblemStatusResponseDto.fromEntity(updated));
    }
}

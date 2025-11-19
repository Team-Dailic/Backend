package graduation_project.Dailic.service;


import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.UserProblemStatus;
import graduation_project.Dailic.repository.ProblemRepository;
import graduation_project.Dailic.repository.UserProblemStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final UserService userService; // 👈 1. UserService 주입
    private final UserProblemStatusRepository userProblemStatusRepository; // 👈 2. UserProblemStatusRepository 주입

    // 문제 저장
    @Transactional
    public Problem save(Problem problem) {
        return problemRepository.save(problem);
    }

    // 문제 전체 조회
    public List<Problem> findAll() {
        return problemRepository.findAll();
    }

    // 문제 ID로 조회
    public Optional<Problem> findById(Long id) {
        return problemRepository.findById(id);
    }

    public List<Problem> findRandomProblemEntities(int count) {
        return problemRepository.findRandomProblems(count);
    }

    // 문제 삭제
    @Transactional
    public void deleteProblem(Long id) {problemRepository.deleteById(id);}

    // License를 기준으로 랜덤 문제 N개 조회
    public List<Problem> findRandomProblemEntitiesByLicense(License license, int count) {
        Pageable pageable = PageRequest.of(0, count);
        return problemRepository.findRandomProblemsByLicense(license, pageable);
    }

    /**
     * 특정 문제의 스크랩 여부를 확인합니다.
     */
    public boolean isProblemScraped(User user, Problem problem) {
        // 3. UserProblemStatusRepository를 사용하여 스크랩 여부 조회
        return userProblemStatusRepository.findByUserAndProblem(user, problem)
                .map(UserProblemStatus::getIsScraped)
                .orElse(false); // 기록이 없으면 false 반환
    }

    // 4. DTO 기반 단건 조회 withSolution 처리 (userId 추가 및 로직 변경)
    public ProblemDto getProblemDtoById(Long id, boolean withSolution, Long userId) { // 👈 userId 매개변수 추가
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 문제가 존재하지 않습니다: " + id));

        // User 객체 조회
        User user = userService.getUserById(userId); // UserService를 사용하여 User 조회

        // 스크랩 여부 확인
        boolean isScraped = isProblemScraped(user, problem);

        // DTO 생성 시 isScraped 상태 전달
        return ProblemDto.from(problem, withSolution, isScraped); // 👈 ProblemDto.from 메서드에 isScraped 전달
    }

    // 5. 랜덤 문제 DTO 반환 메서드 (userId 추가 및 로직 변경)
    public List<ProblemDto> getRandomProblems(int count, Long userId) { // 👈 userId 매개변수 추가
        List<Problem> problems = problemRepository.findRandomProblems(count);

        User user = userService.getUserById(userId);

        return problems.stream()
                .map(problem -> {
                    // 각 문제별 스크랩 여부 조회
                    boolean isScraped = isProblemScraped(user, problem);
                    return ProblemDto.from(problem, false, isScraped); // 👈 isScraped 전달
                })
                .collect(Collectors.toList());
    }
}


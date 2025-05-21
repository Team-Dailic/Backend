package graduation_project.Dailic.service;


import graduation_project.Dailic.controller.DTO.ProblemDto;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
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

    //DTO 기반 단건 조회 withSolution 처리
    public ProblemDto getProblemDtoById(Long id, boolean withSolution) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 문제가 존재하지 않습니다: " + id));
        return ProblemDto.from(problem, withSolution);
    }

    //랜덤 문제 DTO 반환 메서드
    public List<ProblemDto> getRandomProblems(int count) {
        List<Problem> problems = problemRepository.findRandomProblems(count);
        return problems.stream()
                .map(problem->ProblemDto.from(problem, false))
                .collect(Collectors.toList());
    }
}


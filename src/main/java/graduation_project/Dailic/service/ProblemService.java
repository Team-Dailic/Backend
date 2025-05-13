package graduation_project.Dailic.service;


import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // 문제 삭제
    @Transactional
    public void deleteProblem(Long id) {problemRepository.deleteById(id);}


}


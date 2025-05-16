package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.DailyProblem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.DailyProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyProblemService {

    private final DailyProblemRepository dailyProblemRepository;

    // 특정 유저의 특정 날짜 문제 전체 조회
    public List<DailyProblem> getDailyProblemsForUser(User user, LocalDate date) {
        return dailyProblemRepository.findByUserAndDateOrderBySequenceNumberAsc(user, date);
    }

    // 특정 유저의 특정 날짜 특정 순번 문제 조회
    public DailyProblem getProblemByUserAndSequence(User user, LocalDate date, int sequenceNumber) {
        return dailyProblemRepository.findByUserAndDateAndSequenceNumber(user, date, sequenceNumber)
                .orElseThrow(() -> new EntityNotFoundException("해당 문제를 찾을 수 없습니다."));
    }

    // 문제 저장
    public DailyProblem saveDailyProblem(DailyProblem dailyProblem) {
        return dailyProblemRepository.save(dailyProblem);
    }

    // 특정 날짜 전체 유저 문제 조회
    public List<DailyProblem> getAllProblemsForDate(LocalDate date) {
        return dailyProblemRepository.findByDate(date);
    }
}

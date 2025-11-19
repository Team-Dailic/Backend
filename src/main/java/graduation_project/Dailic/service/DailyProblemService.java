package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.DailyProblem;
import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.repository.DailyProblemRepository;
import graduation_project.Dailic.repository.ProblemRepository;
import graduation_project.Dailic.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyProblemService {

    private final DailyProblemRepository dailyProblemRepository;
    private final ProblemRepository problemRepository; //  추가
    private final LicenseService licenseService;       //  추가
    private final UserRepository userRepository;       //  추가

    @Transactional
    public List<DailyProblem> createDailyProblems(Long userId, int count) {
        LocalDate today = LocalDate.now();

        // 1. User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 2. 이미 오늘 문제가 생성되었는지 확인
        List<DailyProblem> existingProblems = dailyProblemRepository.findByUserAndDateOrderBySequenceNumberAsc(user, today);
        if (!existingProblems.isEmpty()) {
            log.info("Daily problems for user {} already exist today. Returning existing list.", userId);
            return existingProblems;
        }

        // 3. 현재 선택된 License 객체 조회
        License currentLicense = licenseService.getCurrentLicenseSelection(userId).getLicense();

        // 4. 해당 License에 맞는 랜덤 문제 조회
        Pageable pageable = PageRequest.of(0, count);
        List<Problem> randomProblems = problemRepository.findRandomProblemsByLicense(currentLicense, pageable);

        if (randomProblems.isEmpty()) {
            throw new EntityNotFoundException(currentLicense.getName() + "에 등록된 문제가 없습니다.");
        }

        // 5. DailyProblem 엔티티 생성 및 저장
        List<DailyProblem> newDailyProblems = new ArrayList<>();
        int sequence = 1;
        for (Problem problem : randomProblems) {
            DailyProblem dailyProblem = DailyProblem.builder()
                    .user(user)
                    .problem(problem) // Problem 엔티티 참조
                    .date(today)
                    .sequenceNumber(sequence++)
                    .solved(false) // 초기값은 false
                    .build();
            newDailyProblems.add(dailyProblemRepository.save(dailyProblem));
        }

        log.info("Successfully created {} new daily problems for user {} (License: {}).", count, userId, currentLicense.getName());
        return newDailyProblems;
    }


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
    @Transactional
    public DailyProblem saveDailyProblem(DailyProblem dailyProblem) {
        return dailyProblemRepository.save(dailyProblem);
    }

    // 특정 날짜 전체 유저 문제 조회
    public List<DailyProblem> getAllProblemsForDate(LocalDate date) {
        return dailyProblemRepository.findByDate(date);
    }
}

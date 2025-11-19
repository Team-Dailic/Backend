package graduation_project.Dailic.service;

import graduation_project.Dailic.controller.DTO.CurrentLicenseDto;
import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.Occupation;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.*;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseSelectionRepository licenseRepo;
    private final UserProblemStatusRepository statusRepo;
    private final ProblemRepository problemRepository; // 👈 2. ProblemRepository 주입
    private final LicenseRepository licenseRepository;
    private final UserRepository userRepository; // ✅ 추가
    private final DailyProblemRepository dailyProblemRepository;

    @Transactional
    public LicenseSelection register(User user, Occupation occupation, String licenseName) {

        // ✅ user를 영속 상태로 다시 조회
        User persistedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        License license = licenseRepository.findByName(licenseName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자격증입니다."));

        LicenseSelection ls = new LicenseSelection(null, persistedUser, license, occupation, LocalDateTime.now());
        return licenseRepo.save(ls);
    }

    @Transactional
    public LicenseSelection updateLicense(User user, Occupation occupation, String licenseName) {
        LicenseSelection ls = licenseRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("선택 정보가 없습니다."));

        License license = licenseRepository.findByName(licenseName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자격증입니다."));

        ls.setOccupation(occupation);
        ls.setLicense(license);

        // 자격증 변경 시, 오늘 날짜의 데일리 문제 강제 삭제

        dailyProblemRepository.deleteByUserAndDate(user, LocalDate.now());

        return ls;
    }

    public CurrentLicenseDto getCurrentLicenseDto(Long userId){
        LicenseSelection selection = getCurrentLicenseSelection(userId);
        License currentLicense = selection.getLicense();

        // 3. totalQuestion 계산 로직 수정
        // ProblemRepository에서 해당 License의 전체 문제 개수를 가져옴
        int total = problemRepository.countByLicense(currentLicense);

        // solvedQuestion은 기존 UserProblemStatusRepository의 쿼리 사용
        int solved = statusRepo.countSolvedProblemsByUserAndLicense(userId, currentLicense);

        return new CurrentLicenseDto(
                selection.getOccupation().name(),
                currentLicense.getName(),
                total, // ✨ 이제 전체 문제 개수가 들어갑니다.
                solved
        );
    }

    public LicenseSelection getCurrentLicenseSelection(Long userId) {
        return licenseRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("선택된 자격증이 없습니다."));
    }
}

package graduation_project.Dailic.service;

import graduation_project.Dailic.controller.DTO.CurrentLicenseDto;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.Occupation;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.LicenseSelectionRepository;
import graduation_project.Dailic.repository.UserProblemStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseSelectionRepository licenseRepo;
    private final UserProblemStatusRepository statusRepo;

    public LicenseSelection register(User user, Occupation occupation, String license) {
        LicenseSelection ls = new LicenseSelection(null, user, license, occupation, LocalDateTime.now());
        return licenseRepo.save(ls);
    }

    @Transactional
    public LicenseSelection updateLicense(User user, Occupation occupation, String license) {
        LicenseSelection ls = licenseRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("선택 정보가 없습니다."));
        ls.setOccupation(occupation);
        ls.setLicense(license);

        return ls;
    }

    public CurrentLicenseDto getCurrentLicenseDto(Long userId){
        LicenseSelection selection = licenseRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("선택된 자격증이 없습니다."));

        int total = statusRepo.countTotalProblemsByUserId(userId);
        int solved = statusRepo.countSolvedProblemsByUserId(userId);

        return new CurrentLicenseDto(
                selection.getOccupation().name(),
                selection.getLicense(),
                total,
                solved
        );
    }
}

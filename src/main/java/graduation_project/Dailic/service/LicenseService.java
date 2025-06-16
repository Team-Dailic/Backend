package graduation_project.Dailic.service;

import graduation_project.Dailic.controller.DTO.CurrentLicenseDto;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.LicenseSelectionRepository;
import graduation_project.Dailic.repository.UserProblemStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseSelectionRepository licenseRepo;
    private final UserProblemStatusRepository statusRepo;

    public LicenseSelection register(User user, String occupation, String license) {
        LicenseSelection ls = new LicenseSelection(null, user, occupation, license, LocalDateTime.now());
        return licenseRepo.save(ls);
    }

    public CurrentLicenseDto getCurrentLicenseDto(Long userId){
        LicenseSelection selection = licenseRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("선택된 자격증이 없습니다."));

        int total = statusRepo.countTotalProblemsByUserId(userId);
        int solved = statusRepo.countSolvedProblemsByUserId(userId);

        return new CurrentLicenseDto(
                selection.getOccupation(),
                selection.getLicense(),
                total,
                solved
        );
    }
}

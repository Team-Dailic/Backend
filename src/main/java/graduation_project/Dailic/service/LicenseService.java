package graduation_project.Dailic.service;

import graduation_project.Dailic.controller.DTO.CurrentLicenseDto;
import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.Occupation;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.LicenseRepository;
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
    private final LicenseRepository licenseRepository;

    public LicenseSelection register(User user, Occupation occupation, String licenseName) {

        License license = licenseRepository.findByName(licenseName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자격증입니다."));

        LicenseSelection ls = new LicenseSelection(null, user, license, occupation, LocalDateTime.now());
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

        return ls;
    }

    public CurrentLicenseDto getCurrentLicenseDto(Long userId){
        LicenseSelection selection = licenseRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("선택된 자격증이 없습니다."));

        int total = statusRepo.countTotalProblemsByUserId(userId);
        int solved = statusRepo.countSolvedProblemsByUserId(userId);

        return new CurrentLicenseDto(
                selection.getOccupation().name(),
                selection.getLicense().getName(),
                total,
                solved
        );
    }
}

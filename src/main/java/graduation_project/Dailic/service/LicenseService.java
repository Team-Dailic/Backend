package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.repository.LicenseSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseSelectionRepository repo;

    public LicenseSelection register(User user, String occupation, String license) {
        LicenseSelection ls = new LicenseSelection(null, user, occupation, license, LocalDateTime.now());
        return repo.save(ls);
    }
}

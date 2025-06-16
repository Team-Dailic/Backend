package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.LicenseSelection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseSelectionRepository extends JpaRepository<LicenseSelection, Long> {
    Optional<LicenseSelection> findByUserId(Long userId);
}

package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findByName(String name);
}

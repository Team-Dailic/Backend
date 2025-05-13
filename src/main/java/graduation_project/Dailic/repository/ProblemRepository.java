package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by question text or other attributes
}

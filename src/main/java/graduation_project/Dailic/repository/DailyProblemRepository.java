package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.DailyProblem;
import graduation_project.Dailic.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyProblemRepository extends JpaRepository<DailyProblem, Long> {

    List<DailyProblem> findByUserAndDateOrderBySequenceNumberAsc(User user, LocalDate date);

    Optional<DailyProblem> findByUserAndDateAndSequenceNumber(User user, LocalDate date, int sequenceNumber);

    List<DailyProblem> findByDate(LocalDate date);

    @Transactional
    void deleteByUserAndDate(User user, LocalDate date);
}

package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    //랜덤 문제 쿼리
    @Query(value = "SELECT * FROM problem ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Problem> findRandomProblems(@Param("count") int count);
}

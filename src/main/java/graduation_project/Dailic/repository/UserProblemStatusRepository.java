package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.UserProblemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProblemStatusRepository extends JpaRepository<UserProblemStatus, Long> {

    // 특정 유저와 문제에 대한 UserProblemStatus 조회
    Optional<UserProblemStatus> findByUserAndProblem(User user, Problem problem);

    // 유저가 푼 문제 목록을 조회
    List<UserProblemStatus> findByUser(User user);

    // 유저가 맞춘 문제 목록 조회
    List<UserProblemStatus> findByUserAndIsCorrectTrue(User user);

    // 유저가 틀린 문제 목록 조회
    List<UserProblemStatus> findByUserAndIsCorrectFalse(User user);

    // 유저가 스크랩한 문제 목록 조회
    List<UserProblemStatus> findByUserAndIsScrapedTrue(User user);

    @Query("SELECT COUNT(u) FROM UserProblemStatus u WHERE u.user.id = :userId")
    int countTotalProblemsByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM UserProblemStatus u WHERE u.user.id = :userId AND u.isCorrect = true")
    int countSolvedProblemsByUserId(Long userId);
}

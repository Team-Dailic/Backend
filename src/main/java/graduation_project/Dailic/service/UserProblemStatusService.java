package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.domain.UserProblemStatus;
import graduation_project.Dailic.repository.UserProblemStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProblemStatusService {

    private final UserProblemStatusRepository userProblemStatusRepository;

    // 특정 유저와 문제에 대한 UserProblemStatus 조회
    public Optional<UserProblemStatus> getUserProblemStatus(User user, Problem problem) {
        return userProblemStatusRepository.findByUserAndProblem(user, problem);
    }

    // 특정 유저가 푼 문제 목록 조회
    public List<UserProblemStatus> getUserProblemStatusByUser(User user) {
        return userProblemStatusRepository.findByUser(user);
    }

    // 유저가 풀었고, 정답인 문제 목록 조회
    public List<UserProblemStatus> getCorrectUserProblemStatuses(User user) {
        return userProblemStatusRepository.findByUserAndIsCorrectTrue(user);
    }

    // 유저의 오답 문제 목록을 반환
    public List<UserProblemStatus> getIncorrectProblems(User user, License license) {
        return userProblemStatusRepository.findIncorrectProblemsByUserAndLicense(user, license);
    }

    // 유저가 스크랩한 문제 상태 목록 반환
    public List<UserProblemStatus> getScrapedProblems(User user, License license) {
        return userProblemStatusRepository.findScrapedProblemsByUserAndLicense(user, license);
    }

    // UserProblemStatus 새로 저장
    @Transactional
    public UserProblemStatus saveUserProblemStatus(UserProblemStatus userProblemStatus) {
        return userProblemStatusRepository.save(userProblemStatus);
    }

    // 문제 풀이 상태 수정
    @Transactional
    public UserProblemStatus updateUserProblemStatus(UserProblemStatus existingStatus, Boolean isCorrect, String userAnswer, Boolean isRetried) {
        existingStatus.setIsCorrect(isCorrect);
        existingStatus.setUserAnswer(userAnswer);
        existingStatus.setIsRetried(isRetried);
        existingStatus.setAnsweredAt(LocalDateTime.now());
        return userProblemStatusRepository.save(existingStatus);
    }
}

package graduation_project.Dailic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id"}))
public class UserProblemStatus {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Problem problem;

    // 정답 여부
    @Column(nullable = false)
    private Boolean isCorrect;

    // 스크랩 여부
    @Column(nullable = false)
    private Boolean isScraped;

    // 사용자의 선택
    private String userAnswer;

    // 다시 풀었는지 여부
    @Column(nullable = false)
    private Boolean isRetried;

    @Column(nullable = false)
    private LocalDateTime answeredAt;

}

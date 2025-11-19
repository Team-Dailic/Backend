package graduation_project.Dailic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyProblem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Problem problem;

    // 출제된 날짜
    @Column(nullable = false)
    private LocalDate date;

    // 1~20번 문제 순서
    @Column(nullable = false)
    private int sequenceNumber;

    @Column(nullable = false)
    private boolean solved = false;
}

package graduation_project.Dailic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Problem {
    @Id @GeneratedValue
    private Long id;

    // License와의 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private License license;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;

    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String solution;
}

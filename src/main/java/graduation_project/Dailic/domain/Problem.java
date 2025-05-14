package graduation_project.Dailic.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Problem {
    @Id @GeneratedValue
    private Long id;

    private String questionText;

    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Stirng option5;

    private String correctAnswer;
}

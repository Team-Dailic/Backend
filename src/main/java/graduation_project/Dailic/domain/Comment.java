package graduation_project.Dailic.domain;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id @GeneratedValue
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
}

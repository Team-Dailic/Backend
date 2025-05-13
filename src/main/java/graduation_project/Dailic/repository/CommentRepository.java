package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByProblemId(Long problemId);
}

package graduation_project.Dailic.service;

import graduation_project.Dailic.domain.Comment;
import graduation_project.Dailic.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 저장
    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    // 댓글 전체 조회
    public List<Comment> findByProblemId(Long problemId) {
        return commentRepository.findByProblemId(problemId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}

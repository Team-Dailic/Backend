package graduation_project.Dailic.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation_project.Dailic.controller.DTO.ProblemCreateRequest;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemImportService {
    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public BulkImportResponse bulkCreateFromRawJson(String rawJsonArray) {
        try {
            // JSON 배열을 요청 DTO 리스트러 파싱
            var reqs = objectMapper.readValue(
                    rawJsonArray,
                    new TypeReference<List<graduation_project.Dailic.controller.DTO.ProblemCreateRequest>>() {
                    }
            );
            return bulkCreate(reqs);
        } catch (Exception e) {
            return new BulkImportResponse(
                    List.of(),
                    List.of(new RowError(-1, "JSON 파싱 실패: " + e.getMessage()))
            );
        }
    }

    @Transactional
    public BulkImportResponse bulkCreate(List<ProblemCreateRequest> reqs) {
        List<Long> createdIds = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();

        int i = 0;
        for(ProblemCreateRequest r : reqs) {
            i++;
            try {
                // 최소 검증
                if (r.correctAnswer() < 1 || r.correctAnswer() > 5)
                    throw new IllegalArgumentException("correctAnswer must be 1...5");
                if (r.correctAnswer() == 5 && (r.option5() == null || r.option5().isBlank()))
                    throw new IllegalArgumentException("correctAnswer=5 but option5 is empty");

                Problem p = new Problem();
                p.setQuestionText(r.questionText());
                p.setOption1(r.option1());
                p.setOption2(r.option2());
                p.setOption3(r.option3());
                p.setOption4(r.option4());
                p.setOption5(r.option5());
                p.setCorrectAnswer(String.valueOf(r.correctAnswer()));
                p.setSolution(r.solution());

                createdIds.add(problemRepository.save(p).getId());
            } catch (Exception e) {
                errors.add(new RowError(i, e.getMessage()));
            }
        }
        return new BulkImportResponse(createdIds, errors);
    }

    public record RowError(int index, String message) {}
    public record BulkImportResponse(List<Long> createdIds, List<RowError> errors) {}
}

package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ApiResponse;
import graduation_project.Dailic.controller.DTO.ProblemCreateRequest;
import graduation_project.Dailic.controller.DTO.UrlImportRequest;
import graduation_project.Dailic.service.ProblemImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemImportController {
    private final ProblemImportService importService;
    private final RestTemplate restTemplate;

    //JSON 배열로 bulk 업로드
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<ProblemImportService.BulkImportResponse>> bulkCreate(
            @Valid @RequestBody List<ProblemCreateRequest> reqs) {
        var result = importService.bulkCreate(reqs);
        String msg = "생성: %d개, 실패: %d개".formatted(result.createdIds().size(), result.errors().size());
        return ResponseEntity.ok(new ApiResponse<>(200, msg, result));
    }

    // URL에서 JSON 배열을 내려받아 저장
    @PostMapping("/bulk-from-url")
    public ResponseEntity<ApiResponse<ProblemImportService.BulkImportResponse>> bulkCreateFromUrl(
            @Valid @RequestBody UrlImportRequest req) {
        var resp = restTemplate.getForEntity(req.url(), String.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(502, "원격에서 JSON을 받지 못했습니다.", null));
        }
        var result = importService.bulkCreateFromRawJson(resp.getBody());
        String msg = "생성: %d개, 실패: %d개".formatted(result.createdIds().size(), result.errors().size());
        return ResponseEntity.ok(new ApiResponse<>(200, msg,result));
    }
}

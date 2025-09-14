package graduation_project.Dailic.service;

import graduation_project.Dailic.config.OpenAIConfig;
import graduation_project.Dailic.controller.DTO.OpenAiRequest;
import graduation_project.Dailic.controller.DTO.OpenAiResponse;
import graduation_project.Dailic.controller.DTO.ProblemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate openAiRestTemplate;

    @Value("${openai.api-url}")
    private String apiUrl;

   @Value("${openai.model}")
    private String model;

    /**
     * 자유 질문 API
     */

    public String ask(String question) {
        try {
            OpenAiRequest request = new OpenAiRequest(
                    model,
                    List.of(new OpenAiRequest.Message("user", question))
            );

            log.info("Sending request to OpenAI: {}", request);
            log.info("Using model: {}", model);

            // 🔽 명시적으로 헤더 세팅
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth("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"); // 🔥 여기에 @Value("${openai.api-key}") 넣기

            HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

            RestTemplate restTemplate = new RestTemplate(); // 🔥 Interceptor 없이 새로 생성
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );

            log.info("Response from OpenAI: {}", response);

            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            }
            return "응답을 가져올 수 없습니다.";
        } catch (HttpClientErrorException e) {
            log.error("OpenAI API 호출 오류: {}", e.getResponseBodyAsString());
            return "OpenAI API 호출 실패: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return "서버 내부 오류가 발생했습니다.";
        }
    }

    /**
     * 문제 해설 API

    public String explain(ProblemDto problem, boolean includeSolution) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 객관식 문제를 단계별로 한국어로 친절하게 설명해줘.\n");
        prompt.append("문제: ").append(problem.getQuestionText()).append("\n");
        prompt.append("보기:\n");
        for (int i = 0; i < problem.getOptions().size(); i++) {
            prompt.append((i + 1)).append(") ").append(problem.getOptions().get(i)).append("\n");
        }
        if (includeSolution && problem.getCorrectAnswer() != null) {
            prompt.append("정답: ").append(problem.getCorrectAnswer()).append("\n");
        }
        if (includeSolution && problem.getSolution() != null) {
            prompt.append("기존 해설: ").append(problem.getSolution()).append("\n");
        }

        OpenAiRequest request = new OpenAiRequest(
                model,
                List.of(new OpenAiRequest.Message("user", prompt.toString()))
        );

        ResponseEntity<OpenAiResponse> response = openAiRestTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(request),
                OpenAiResponse.class
        );

        if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        return "해설을 생성할 수 없습니다.";
    }
     */
}

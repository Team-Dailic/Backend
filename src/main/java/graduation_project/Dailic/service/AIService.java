package graduation_project.Dailic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import graduation_project.Dailic.config.OpenAIProperties;
import graduation_project.Dailic.controller.DTO.OpenAiRequest;
import graduation_project.Dailic.controller.DTO.OpenAiResponse;
import graduation_project.Dailic.controller.DTO.ProblemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate openAiRestTemplate;
    private final OpenAIProperties properties;

    public String ask(String question) {
        try {
            // 요청 DTO
            OpenAiRequest request = new OpenAiRequest(
                    properties.getModel(),
                    List.of(new OpenAiRequest.Message("user", question))
            );

            // JSON 직렬화 로그
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);
            log.info("Serialized JSON: {}", jsonBody);

            // 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // OpenAI API 호출
            ResponseEntity<OpenAiResponse> response = openAiRestTemplate.exchange(
                    properties.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );

            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            }
            return "응답을 가져올 수 없습니다.";
        } catch (HttpClientErrorException e) {
            log.error("OpenAI 호출 오류: {}", e.getResponseBodyAsString());
            return "OpenAI 호출 실패: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return "서버 내부 오류 발생";
        }
    }

    /**
     * 🔹 문제 해설
     */
    public String explain(ProblemDto problem, boolean includeSolution) {
        try {
            // 🔥 프롬프트 구성
            StringBuilder prompt = new StringBuilder();
            prompt.append("다음 객관식 문제를 단계별로 한국어로 해설해줘.\n");
            prompt.append("문제: ").append(problem.getQuestionText()).append("\n");
            prompt.append("보기:\n");
            for (int i = 0; i < problem.getOptions().size(); i++) {
                prompt.append((i + 1)).append(") ").append(problem.getOptions().get(i)).append("\n");
            }
            if (includeSolution && problem.getCorrectAnswer() != null) {
                String answerNum = problem.getCorrectAnswer().replace("option", "");
                prompt.append("정답: ").append(answerNum).append("\n");
            }
            if (includeSolution && problem.getSolution() != null) {
                prompt.append("기존 해설: ").append(problem.getSolution()).append("\n");
            }

            // 요청 DTO
            OpenAiRequest request = new OpenAiRequest(
                    properties.getModel(),
                    List.of(new OpenAiRequest.Message("user", prompt.toString()))
            );

            // JSON 직렬화
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);
            log.info("Serialized JSON: {}", jsonBody);

            // 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // 요청 전송
            ResponseEntity<OpenAiResponse> response = openAiRestTemplate.exchange(
                    properties.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );

            // 응답 파싱
            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            }
            return "해설을 가져올 수 없습니다.";
        } catch (HttpClientErrorException e) {
            log.error("OpenAI 호출 오류: {}", e.getResponseBodyAsString());
            return "OpenAI 호출 실패: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return "서버 내부 오류 발생";
        }
    }
}

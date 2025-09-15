package graduation_project.Dailic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import graduation_project.Dailic.config.OpenAIProperties;
import graduation_project.Dailic.controller.DTO.OpenAiRequest;
import graduation_project.Dailic.controller.DTO.OpenAiResponse;
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
}

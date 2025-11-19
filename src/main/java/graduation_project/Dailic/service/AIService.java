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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate openAiRestTemplate;
    private final OpenAIProperties properties;
    private final ProblemService problemService;

    public String ask(Long problemId, Long userId, String licenseName, String question) {
        try {
            // 1. ProblemService를 통해 문제 내용 (ProblemDto) 조회
            ProblemDto problem = problemService.getProblemDtoById(problemId, false, userId);

            // 2. 시스템 프롬프트 및 사용자 질문 통합
            String systemPrompt = String.format(
                    "당신은 '%s' 시험 대비 학습 멘토입니다. 당신이 현재 참조하고 있는 문제는 아래와 같습니다. 사용자의 질문에 대해 현재 참조 중인 시험 문맥과 문제 내용을 기반으로 전문적으로 답변해 주세요. 답변은 간결하고 명확해야 합니다.",
                    licenseName
            );

            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("--- 참조 문제 내용 ---\n");

            // 문제 내용 Null 체크
            if (problem.getQuestionText() == null) {
                return "참조할 문제 내용이 없어 질문을 처리할 수 없습니다.";
            }

            userPrompt.append("문제: ").append(problem.getQuestionText()).append("\n");

            if (problem.getOptions() != null && !problem.getOptions().isEmpty()) {
                userPrompt.append("보기:\n");
                for (int i = 0; i < problem.getOptions().size(); i++) {
                    userPrompt.append((i + 1)).append(") ").append(problem.getOptions().get(i)).append("\n");
                }
            }

            userPrompt.append("----------------------\n");
            userPrompt.append("사용자 질문: ").append(question);

            // 3. 요청 메시지 구성
            List<OpenAiRequest.Message> messages = List.of(
                    new OpenAiRequest.Message("system", systemPrompt),
                    new OpenAiRequest.Message("user", userPrompt.toString())
            );

            OpenAiRequest request = new OpenAiRequest(
                    properties.getModel(),
                    messages
            );


            // JSON 직렬화 로그
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);
            log.info("Serialized JSON: {}", jsonBody);

            // 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getKey());

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // OpenAI API 호출
            ResponseEntity<OpenAiResponse> response = openAiRestTemplate.exchange(
                    properties.getUrl(),
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
    public String explain(ProblemDto problem, boolean includeSolution, String licenseName) { // ✨ licenseName 파라미터 추가
        try {
            // 1. 시스템 프롬프트 설정 (licenseName 활용)
            String systemPrompt = String.format(
                    "당신은 '%s' 시험 대비 학습 멘토입니다. 다음 객관식 문제를 단계별로 상세하고 친절하게 한국어로 해설해 주세요.",
                    licenseName
            );

            // 2. 사용자 요청 내용 구성
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("문제: ").append(problem.getQuestionText()).append("\n");
            userPrompt.append("보기:\n");
            for (int i = 0; i < problem.getOptions().size(); i++) {
                userPrompt.append((i + 1)).append(") ").append(problem.getOptions().get(i)).append("\n");
            }

            // 정답 및 기존 해설 정보 추가
            if (includeSolution && problem.getCorrectAnswer() != null) {
                String answerNum = problem.getCorrectAnswer().replace("option", "");
                userPrompt.append("정답: ").append(answerNum).append("\n");
            }
            if (includeSolution && problem.getSolution() != null) {
                userPrompt.append("기존 해설: ").append(problem.getSolution()).append("\n");
            }

            // 3. 요청 메시지 DTO 구성
            List<OpenAiRequest.Message> messages = new ArrayList<>();
            messages.add(new OpenAiRequest.Message("system", systemPrompt)); // ✨ System Prompt 추가
            messages.add(new OpenAiRequest.Message("user", userPrompt.toString()));

            OpenAiRequest request = new OpenAiRequest(
                    properties.getModel(),
                    messages
            );

            // JSON 직렬화
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);
            log.info("Serialized JSON: {}", jsonBody);

            // 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getKey());

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // 요청 전송
            ResponseEntity<OpenAiResponse> response = openAiRestTemplate.exchange(
                    properties.getUrl(),
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

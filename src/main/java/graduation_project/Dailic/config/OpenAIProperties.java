package graduation_project.Dailic.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {
    private String apiKey;
    private String apiUrl;
    private String model;

    @PostConstruct
    public void logProperties() { // 애플리케이션 시작 시 설정값 출력 (디버깅용)
        System.out.println("=== OpenAI 설정 확인 ===");
        System.out.println("API Key: " + (apiKey == null ? "NULL" : apiKey.substring(0, 8) + "..."));
        System.out.println("API URL: " + apiUrl);
        System.out.println("Model: " + model);
        System.out.println("=======================");
    }
}
package graduation_project.Dailic.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduation_project.Dailic.domain.License;
import graduation_project.Dailic.domain.Problem;
import graduation_project.Dailic.repository.LicenseRepository;
import graduation_project.Dailic.repository.ProblemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProblemDataLoader {

    private final ProblemRepository problemRepository;
    private final LicenseRepository licenseRepository;

    @PostConstruct
    public void loadProblemData() throws IOException {

        License driverLicense = licenseRepository.findByName("운전면허 필기시험")
                .orElseGet(() -> licenseRepository.save(new License(null, "운전면허 필기시험")));

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Problem>> typeRef = new TypeReference<>() {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("problem-data.json");

        if (inputStream != null) {
            List<Problem> problems = mapper.readValue(inputStream, typeRef);

            problems.forEach(problem -> problem.setLicense(driverLicense));

            problemRepository.saveAll(problems);
            System.out.println("✅ 문제 데이터가 DB에 저장되었습니다: " + problems.size() + "개");
        } else {
            System.out.println("⚠️ problem-data.json 파일을 찾을 수 없습니다.");
        }
    }
}
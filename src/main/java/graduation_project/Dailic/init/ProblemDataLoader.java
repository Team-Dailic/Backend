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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProblemDataLoader {

    private final ProblemRepository problemRepository;
    private final LicenseRepository licenseRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    @Transactional
    public void loadAllData() throws IOException {

        if (problemRepository.count() > 0) {
            System.out.println("ℹ️ DB에 이미 문제 데이터가 존재하므로, 데이터 로드를 건너뜁니다.");
            return; // 데이터가 이미 있으므로 로직을 중단합니다.
        }

        // 1. 운전면허 필기시험 데이터 로드
        loadProblemsForLicense("운전면허 필기시험", "problem-data.json");

        // 2. 정보처리기사 데이터 로드
        loadProblemsForLicense("정보처리기사", "info-processing-data.json");
    }

    /**
     * 특정 자격증명과 JSON 파일명을 받아 문제를 로드하는 헬퍼 메서드
     * @param licenseName 자격증 이름 (예: "정보처리기사")
     * @param jsonFileName `resources` 폴더에 있는 JSON 파일명 (예: "info-processing-data.json")
     */
    private void loadProblemsForLicense(String licenseName, String jsonFileName) throws IOException {

        License license = licenseRepository.findByName(licenseName)
                .orElseGet(() -> licenseRepository.save(new License(null, licenseName)));

        TypeReference<List<Problem>> typeRef = new TypeReference<>() {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);

        if (inputStream != null) {
            List<Problem> problems = mapper.readValue(inputStream, typeRef);

            problems.forEach(problem -> problem.setLicense(license));

            problemRepository.saveAll(problems);
            System.out.println("✅ " + licenseName + " 문제 데이터가 DB에 저장되었습니다: " + problems.size() + "개");
        } else {
            System.out.println("⚠️ " + jsonFileName + " 파일을 찾을 수 없습니다.");
        }
    }
}
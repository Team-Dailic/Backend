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
        // 🔹 License 기본 데이터 확보
        ensureBaseLicenses();

        if (problemRepository.count() > 0) {
            System.out.println("ℹ️ DB에 이미 문제 데이터가 존재하므로, 문제 로드를 건너뜁니다.");
            return;
        }

        // 🔹 문제 JSON이 있을 경우만 추가 로드
        loadProblemsForLicense("운전면허증", "problem-data.json");
        loadProblemsForLicense("정보처리기사", "info-processing-data.json");
        loadProblemsForLicense("SQLD", "sqld.json");
    }

    private void ensureBaseLicenses() {
        List<String> baseLicenses = List.of("운전면허증", "한국사능력검정시험", "컴퓨터활용능력 1급","MOS","TOEIC","사회조사분석사","공인노무사","행정사","감정평가사","KBS한국어능력시험","전산회계 1급","전산세무 2급","ERP 정보관리사","FAT","AFPK","간호사 면허","임상병리사","방사선사","물리치료사","병원코디네이터","정보처리기사","SQLD","ADSP","정보보안기사","리눅스마스터 1급");

        for (String name : baseLicenses) {
            licenseRepository.findByName(name)
                    .orElseGet(() -> {
                        License newLicense = new License(null, name);
                        System.out.println("✅ License 등록됨: " + name);
                        return licenseRepository.save(newLicense);
                    });
        }
    }

    /**
     * 특정 자격증명과 JSON 파일명을 받아 문제를 로드하는 헬퍼 메서드
     *
     * @param licenseName  자격증 이름 (예: "정보처리기사")
     * @param jsonFileName `resources` 폴더에 있는 JSON 파일명 (예: "info-processing-data.json")
     */
    private void loadProblemsForLicense(String licenseName, String jsonFileName) throws IOException {

        License license = licenseRepository.findByName(licenseName)
                .orElseGet(() -> licenseRepository.save(new License(null, licenseName)));

        TypeReference<List<Problem>> typeRef = new TypeReference<>() {
        };
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
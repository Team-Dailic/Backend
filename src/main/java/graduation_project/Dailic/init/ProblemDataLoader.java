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

    // ✨ 1. 고정된 더미 문제 개수 상수 정의 (1000 이하의 고정값)
    private static final int DUMMY_PROBLEM_COUNT = 500;

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

        // 2. 더미 데이터 로드
        loadDummyProblems(); // 👈 더미 로드 로직 호출
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

    /**
     * 실제 데이터가 없는 자격증에 더미 문제를 로드하는 로직
     */
    private void loadDummyProblems() {
        // 실제 데이터가 존재하는 자격증 이름 목록
        List<String> realDataLicenses = List.of("운전면허증", "정보처리기사", "SQLD");

        licenseRepository.findAll().forEach(license -> {
            if (!realDataLicenses.contains(license.getName())) {
                // 실제 데이터가 없는 자격증만 처리
                try {
                    // 고정된 개수 DUMMY_PROBLEM_COUNT 사용 (500개)
                    int count = DUMMY_PROBLEM_COUNT;

                    List<Problem> dummyProblems = createDummyProblems(license, count);

                    problemRepository.saveAll(dummyProblems);
                    System.out.println("✅ " + license.getName() + " 더미 문제 데이터가 DB에 저장되었습니다: " + count + "개");
                } catch (Exception e) {
                    System.err.println("❌ 더미 데이터 로드 실패: " + license.getName() + " - " + e.getMessage());
                }
            }
        });
    }

    /**
     * 더미 문제 객체를 생성하는 헬퍼 메서드
     */
    private List<Problem> createDummyProblems(License license, int count) {
        List<Problem> dummyProblems = new java.util.ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Problem dummy = new Problem();

            // ✨ Setter를 사용하여 필드 이름에 맞게 값 설정
            dummy.setLicense(license);
            dummy.setQuestionText("[" + license.getName() + "] 더미 문제 " + i + ": 기본 개념 확인 문제");
            dummy.setOption1("옵션 1");
            dummy.setOption2("옵션 2");
            dummy.setOption3("옵션 3");
            dummy.setOption4("옵션 4");
            dummy.setOption5("옵션 5");
            dummy.setCorrectAnswer("1"); // 정답을 1번 옵션으로 설정 (String 필드이므로 "1" 사용)
            dummy.setSolution("이것은 더미 해설입니다.");

            dummyProblems.add(dummy);
        }
        return dummyProblems;
    }
}
@Component
@RequiredArgsConstructor
public class ProblemDataLoader {

    private final ProblemRepository problemRepository;

    @PostConstruct
    public void loadProblemData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Problem>> typeRef = new TypeReference<>() {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("problem-data.json");

        if (inputStream != null) {
            List<Problem> problems = mapper.readValue(inputStream, typeRef);
            problemRepository.saveAll(problems);
            System.out.println("✅ 문제 데이터가 DB에 저장되었습니다: " + problems.size() + "개");
        } else {
            System.out.println("⚠️ problem-data.json 파일을 찾을 수 없습니다.");
        }
    }
}
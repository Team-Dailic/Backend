package graduation_project.Dailic.controller.DTO;

import graduation_project.Dailic.domain.DailyProblem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DailyProblemDto {
    private int sequenceNumber;
    private ProblemDto problem;

    public static DailyProblemDto from(DailyProblem dp, boolean isScraped) {
        DailyProblemDto dto = new DailyProblemDto();
        dto.setSequenceNumber(dp.getSequenceNumber());

        dto.setProblem(ProblemDto.from(dp.getProblem(), false, isScraped));

        return dto;
    }
}
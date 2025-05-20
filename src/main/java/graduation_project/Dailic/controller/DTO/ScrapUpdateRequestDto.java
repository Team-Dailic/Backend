package graduation_project.Dailic.controller.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapUpdateRequestDto {
    private Long userId;
    private Long problemId;
    private Boolean isScraped;
}

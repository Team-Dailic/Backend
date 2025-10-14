package graduation_project.Dailic.controller;

import graduation_project.Dailic.controller.DTO.ApiResponse;
import graduation_project.Dailic.controller.DTO.CurrentLicenseDto;
import graduation_project.Dailic.controller.DTO.LicenseSelectionRequestDto;
import graduation_project.Dailic.domain.LicenseSelection;
import graduation_project.Dailic.domain.User;
import graduation_project.Dailic.service.LicenseService;
import graduation_project.Dailic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/licenses")
public class LicenseController {

    private final UserService userService;
    private final LicenseService licenseService;

    @PostMapping
    public ResponseEntity<?> selectLicense(
            @RequestParam Long userId,
            @RequestBody LicenseSelectionRequestDto req) {
        User user = userService.getUserById(userId);
        LicenseSelection ls = licenseService.register(
                user, req.getOccupation(), req.getLicense());
        Map<String, Object> data = Map.of(
                "userId", user.getId(),
                "occupation", ls.getOccupation(),
                "license", ls.getLicense().getName()
        );
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                200,
                "학습 자격증이 설정되었습니다.",
                data
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<?> updateLicense(
            @RequestParam Long userId,
            @RequestBody LicenseSelectionRequestDto req) {
        User user = userService.getUserById(userId);
        LicenseSelection ls =
                licenseService.updateLicense(user, req.getOccupation(), req.getLicense());
        Map<String, Object> data = Map.of(
                "userId", user.getId(),
                "occupation", ls.getOccupation(),
                "license", ls.getLicense().getName()
        );
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                200,
                "학습 자격증의 변경되었습니다.",
                data
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentLicense(@RequestParam Long userId) {
        CurrentLicenseDto dto = licenseService.getCurrentLicenseDto(userId);

        ApiResponse<CurrentLicenseDto> response = new ApiResponse<>(
                200,
                "현재 선택된 자격증입니다.",
                dto
        );
        return ResponseEntity.ok(response);
    }
}

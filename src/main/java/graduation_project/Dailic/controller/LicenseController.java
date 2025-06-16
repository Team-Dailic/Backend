package graduation_project.Dailic.controller;

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
        Map<String, Object> body = Map.of(
                "userId", user.getId(),
                "occupation", ls.getOccupation(),
                "license", ls.getLicense(),
                "status", "SELECTED",
                "message", "학습 자격증이 설정되었습니다."
        );
        return ResponseEntity.ok(body);
    }
}

package graduation_project.Dailic.controller.DTO;

import jakarta.validation.constraints.NotBlank;

public record UrlImportRequest (
    @NotBlank String url
) {}

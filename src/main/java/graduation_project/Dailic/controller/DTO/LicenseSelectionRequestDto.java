package graduation_project.Dailic.controller.DTO;

import graduation_project.Dailic.domain.Occupation;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LicenseSelectionRequestDto {
    private Occupation occupation;
    private String license;

    public LicenseSelectionRequestDto() {}

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
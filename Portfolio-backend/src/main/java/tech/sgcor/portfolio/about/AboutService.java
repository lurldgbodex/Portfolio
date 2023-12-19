package tech.sgcor.portfolio.about;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Validated
public class AboutService {
    private final AboutRepository aboutRepository;

    public AboutResponse getAbout(Long id) throws ResourceNotFound {
        var about = aboutRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Data not found with id " + id)
                );

        Map<String, String> socials = new HashMap<>();
        socials.put("github", about.getGithub());
        socials.put("linkedin", about.getLinkedin());
        socials.put("medium", about.getMedium());

        return new AboutResponse(
                about.getId(),
                about.getName(),
                about.getTitle(),
                about.getAddress(),
                about.getEmail(),
                about.getDob().toString(),
                about.getPhoneNumber(),
                about.getSummary(),
                socials
        );
    }

    public About add(@Valid CreateRequest request) {
        var about = About.builder()
                .name(request.getName())
                .email(request.getEmail())
                .dob(request.getDob())
                .title(request.getTitle())
                .address(request.getAddress())
                .phoneNumber(request.getPhone_number())
                .summary(request.getSummary())
                .github(request.getGithub())
                .linkedin(request.getLinkedin())
                .medium(request.getMedium())
                .build();

        return aboutRepository.save(about);

    }

    public CustomResponse update(
            Long id, @Valid UpdateRequest request) throws ResourceNotFound, BadRequestException {
        var about = aboutRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Data not found with id " + id)
                );
        if (request == null) {
            throw new BadRequestException("you need to provide the field(s) you want to update");
        }

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getTitle(), ""),
                Objects.toString(request.getPhone_number(), ""),
                Objects.toString(request.getDob(), ""),
                Objects.toString(request.getAddress(), ""),
                Objects.toString(request.getEmail(), ""),
                Objects.toString(request.getSummary(), ""),
                Objects.toString(request.getGithub(), ""),
                Objects.toString(request.getLinkedin(), ""),
                Objects.toString(request.getMedium(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("At least one field must be non-blank to perform the update");
        }


        about.setName(isNotBlank(request.getName()) ? request.getName() : about.getName());
        about.setTitle(isNotBlank(request.getTitle()) ? request.getTitle() : about.getTitle());
        about.setPhoneNumber(isNotBlank(request.getPhone_number()) ? request.getPhone_number() : about.getPhoneNumber());
        about.setDob((request.getDob() != null) ? request.getDob() : about.getDob());
        about.setAddress(isNotBlank(request.getAddress()) ? request.getAddress() : about.getAddress());
        about.setEmail(isNotBlank(request.getEmail()) ? request.getEmail() : about.getEmail());
        about.setSummary(isNotBlank(request.getSummary()) ? request.getSummary() : about.getSummary());
        about.setGithub(isNotBlank(request.getGithub()) ? request.getGithub() : about.getGithub());
        about.setLinkedin(isNotBlank(request.getLinkedin()) ? request.getLinkedin() : about.getLinkedin());
        about.setMedium(isNotBlank(request.getMedium()) ? request.getMedium() : about.getMedium());

         About status = aboutRepository.save(about);

        return new CustomResponse(
                         HttpStatus.OK.value(),
                         "update successful",
                         HttpStatus.OK
                 );
    }

    public CustomResponse delete(Long id) throws ResourceNotFound {
        var about = aboutRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Data not found with id " + id)
                );
        aboutRepository.deleteById(id);
        return new CustomResponse(
                HttpStatus.OK.value(),
                "about successfully deleted with id " + id,
                HttpStatus.OK
        );
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

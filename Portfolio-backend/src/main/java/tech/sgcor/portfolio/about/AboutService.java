package tech.sgcor.portfolio.about;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;
import tech.sgcor.portfolio.user.User;
import tech.sgcor.portfolio.user.UserRepository;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class AboutService {
    private final AboutRepository aboutRepository;
    private final UserRepository userRepository;

    public About getAbout(Long aboutId) {
        return aboutRepository.findAboutWithUserById(aboutId);
    }

    public About add(@Valid CreateRequest request) {
        User user = new User();
        user.setFirstName(request.getFirst_name());
        user.setLastName(request.getLast_name());
        user.setMiddleName(SharedService.isNotBlank(
                request.getMiddle_name()) ? request.getMiddle_name() : null);

        var about = About.builder()
                .user(user)
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
        var about = aboutRepository.findAboutWithUserById(id);

        if (about == null) {
            throw new ResourceNotFound("About not found with id");
        }

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getTitle(), ""),
                Objects.toString(request.getPhone_number(), ""),
                Objects.toString(request.getDob(), ""),
                Objects.toString(request.getAddress(), ""),
                Objects.toString(request.getEmail(), ""),
                Objects.toString(request.getSummary(), ""),
                Objects.toString(request.getGithub(), ""),
                Objects.toString(request.getLinkedin(), ""),
                Objects.toString(request.getMedium(), ""),
                Objects.toString(request.getFirst_name(), ""),
                Objects.toString(request.getLast_name(), ""),
                Objects.toString(request.getMiddle_name(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("At least one field must be non-blank to perform the update");
        }

        Long userId = about.getUser().getId();

        User user = userRepository.findById(userId).orElseThrow();

        user.setFirstName(SharedService.isNotBlank(request.getFirst_name())
                ? request.getFirst_name() : user.getFirstName());
        user.setLastName(SharedService.isNotBlank(request.getLast_name())
                ? request.getLast_name() : user.getLastName());
        user.setMiddleName(SharedService.isNotBlank(request.getMiddle_name())
                ? request.getMiddle_name() : user.getMiddleName());

        about.setUser(user);
        about.setTitle(SharedService.isNotBlank(request.getTitle())
                ? request.getTitle() : about.getTitle());
        about.setPhoneNumber(SharedService.isNotBlank(request.getPhone_number())
                ? request.getPhone_number() : about.getPhoneNumber());
        about.setDob((request.getDob() != null) ? request.getDob() : about.getDob());
        about.setAddress(SharedService.isNotBlank(request.getAddress())
                ? request.getAddress() : about.getAddress());
        about.setEmail(SharedService.isNotBlank(request.getEmail())
                ? request.getEmail() : about.getEmail());
        about.setSummary(SharedService.isNotBlank(request.getSummary())
                ? request.getSummary() : about.getSummary());
        about.setGithub(SharedService.isNotBlank(request.getGithub())
                ? request.getGithub() : about.getGithub());
        about.setLinkedin(SharedService.isNotBlank(request.getLinkedin())
                ? request.getLinkedin() : about.getLinkedin());
        about.setMedium(SharedService.isNotBlank(request.getMedium())
                ? request.getMedium() : about.getMedium());

        aboutRepository.save(about);

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
}

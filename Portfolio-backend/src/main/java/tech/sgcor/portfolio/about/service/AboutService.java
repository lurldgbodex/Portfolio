package tech.sgcor.portfolio.about;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;
import tech.sgcor.portfolio.user.User;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class AboutService {
    private final AboutRepository aboutRepository;

    public About getAbout(Long userId) {
        return aboutRepository.findAboutWithUserById(userId)
                .orElseThrow(() -> new ResourceNotFound("about no dey for user with id " + userId));
    }

    public About add(CreateRequest request) {
        User user = new User();
        user.setFirstName(request.getFirst_name());
        user.setLastName(request.getLast_name());
        user.setMiddleName(SharedService.isNotBlank(
                request.getMiddle_name()) ? request.getMiddle_name() : null);
        user.setImageUrl(request.getImage_url());

        var about = About.builder()
                .user(user)
                .email(request.getEmail())
                .dob(LocalDate.parse(request.getDob()))
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
            Long userId, UpdateRequest request) {
        About about = getAbout(userId);

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
                Objects.toString(request.getImage_url(), ""),
                Objects.toString(request.getMiddle_name(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("wetin u wan update?");
        }

        User user = about.getUser();

        user.setFirstName(SharedService.isNotBlank(request.getFirst_name())
                ? request.getFirst_name() : user.getFirstName());
        user.setLastName(SharedService.isNotBlank(request.getLast_name())
                ? request.getLast_name() : user.getLastName());
        user.setMiddleName(SharedService.isNotBlank(request.getMiddle_name())
                ? request.getMiddle_name() : user.getMiddleName());
        user.setImageUrl(SharedService.isNotBlank(request.getImage_url())
                ? request.getImage_url() : user.getImageUrl());

        about.setUser(user);
        about.setTitle(SharedService.isNotBlank(request.getTitle())
                ? request.getTitle() : about.getTitle());
        about.setPhoneNumber(SharedService.isNotBlank(request.getPhone_number())
                ? request.getPhone_number() : about.getPhoneNumber());
        about.setDob((request.getDob() != null) ? LocalDate.parse(request.getDob()) : about.getDob());
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
                         200,
                         "una update dey successful",
                         HttpStatus.OK
                 );
    }

    public CustomResponse delete(Long userId) {
        About about = getAbout(userId);

        aboutRepository.delete(about);
        return new CustomResponse(
                HttpStatus.OK.value(),
                "una don successfully delete about for user wey get the id " + userId,
                HttpStatus.OK
        );
    }
}

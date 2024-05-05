package tech.sgcor.portfolio.about.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.about.dto.AboutDto;
import tech.sgcor.portfolio.about.repository.AboutRepository;
import tech.sgcor.portfolio.about.dto.CreateRequest;
import tech.sgcor.portfolio.about.dto.UpdateRequest;
import tech.sgcor.portfolio.about.entity.About;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;
import tech.sgcor.portfolio.user.entity.User;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class AboutService {
    private final AboutRepository aboutRepository;

    public About findAbout(Long userId) {
        return aboutRepository.findAboutWithUserById(userId)
                .orElseThrow(() -> new ResourceNotFound("about no dey for user with id " + userId));
    }

    public AboutDto getAbout(Long userId) {
        About about = findAbout(userId);

        return AboutDto
                .builder()
                .id(about.getId())
                .email(about.getEmail())
                .address(about.getAddress())
                .title(about.getTitle())
                .summary(about.getSummary())
                .dob(about.getDob())
                .github(about.getGithub())
                .linkedin(about.getLinkedin())
                .phoneNumber(about.getPhoneNumber())
                .cv(about.getCvUrl())
                .medium(about.getMedium())
                .twitter(about.getTwitter())
                .user(about.getUser())
                .build();
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
                .twitter(request.getTwitter())
                .cvUrl(request.getCv())
                .build();

        return aboutRepository.save(about);
    }

    public CustomResponse update(
            Long userId, UpdateRequest request) {
        About about = findAbout(userId);

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
                Objects.toString(request.getMiddle_name(), ""),
                Objects.toString(request.getTwitter(), ""),
                Objects.toString(request.getCv(), "")
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
        about.setTwitter(SharedService.isNotBlank(request.getTwitter())
                ? request.getTwitter() : about.getTwitter());
        about.setCvUrl(SharedService.isNotBlank(request.getCv())
                ? request.getCv() : about.getCvUrl());

        aboutRepository.save(about);
        return new CustomResponse(
                         200,
                         "una update dey successful",
                         HttpStatus.OK
                 );
    }

    public CustomResponse delete(Long userId) {
        About about = findAbout(userId);

        aboutRepository.delete(about);
        return new CustomResponse(
                HttpStatus.OK.value(),
                "una don successfully delete about for user wey get the id " + userId,
                HttpStatus.OK
        );
    }
}

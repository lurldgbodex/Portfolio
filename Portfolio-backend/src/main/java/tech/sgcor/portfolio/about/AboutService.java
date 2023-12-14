package tech.sgcor.portfolio.about;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
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

    public CustomResponse add(CreateRequest request) {
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

        aboutRepository.save(about);

        return new CustomResponse(
                HttpStatus.CREATED.value(),
                "about added successfully",
                HttpStatus.CREATED
        );
    }

    public CustomResponse update(Long id, UpdateRequest request) throws ResourceNotFound {
        var about = aboutRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Data not found with id " + id)
                );

        about.setName(request.getName() != null ? request.getName() : about.getName());
        about.setTitle(request.getTitle() != null ? request.getTitle() : about.getTitle());
        about.setPhoneNumber(request.getPhone_number() != null ? request.getPhone_number() : about.getPhoneNumber());
        about.setDob(request.getDob() != null ? request.getDob() : about.getDob());
        about.setAddress(request.getAddress() != null ? request.getAddress() : about.getAddress());
        about.setEmail(request.getEmail() != null ? request.getEmail() : about.getEmail());
        about.setSummary(request.getSummary() != null ? request.getSummary() : about.getSummary());
        about.setGithub(request.getGithub() != null ? request.getGithub() : about.getGithub());
        about.setLinkedin(request.getLinkedin() != null ? request.getLinkedin() : about.getLinkedin());
        about.setMedium(request.getMedium() != null ? request.getMedium() : about.getMedium());

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

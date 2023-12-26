package tech.sgcor.portfolio.certification;

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

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
public class CertificationService {
    private final CertificationRepository repository;
    private final CertificationDetailsRepository detailsRepository;

    public List<Certification> getCertifications(Long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public Certification addCertification(@Valid CertificationDto request) {
        Certification certification = new Certification();
        certification.setName(request.getName());
        certification.setBody(request.getBody());
        certification.setDate(request.getDate());
        certification.setUserId(request.getUser_id());

        List<CertificationDetails> detailsList = request.getDetails()
                .stream()
                .map((val) -> {
                  CertificationDetails details = new CertificationDetails();
                  details.setDetails(val);
                  details.setCertification(certification);
                  return details;
                }).toList();
        certification.setDetails(detailsList);
        return repository.save(certification);
    }

    @Transactional
    public CustomResponse updateCertification(
            Long id, UpdateCertification request) throws ResourceNotFound, BadRequestException {
        Certification certification = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Certification not found with id"));

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getBody(), ""),
                Objects.toString(request.getDate(), ""),
                Objects.toString(request.getDetails(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("You need to provide the fields you want to update");
        }

        certification.setName(SharedService.isNotBlank(request.getName()) ?
                request.getName() : certification.getName());
        certification.setBody(SharedService.isNotBlank(request.getBody()) ?
                request.getBody() : certification.getBody());
        certification.setDate(request.getDate() != null
                ? request.getDate() : certification.getDate());

        repository.save(certification);

        List<CertificationDetails> details = certification.getDetails();

        if (request.getDetails() != null) {
            var detailsRequest = request.getDetails();
            int noOfDetailsUpdate = Math.min(details.size(), detailsRequest.size());

            for (int i = 0; i < noOfDetailsUpdate; i++) {
                details.get(i).setDetails(SharedService.isNotBlank(detailsRequest.get(i))
                        ? detailsRequest.get(i) : details.get(i).getDetails());
            }

            if (detailsRequest.size() > noOfDetailsUpdate) {
                for (int i = noOfDetailsUpdate; i < detailsRequest.size(); i++) {
                    CertificationDetails newDetails = new CertificationDetails();
                    newDetails.setDetails(detailsRequest.get(i));
                    newDetails.setCertification(certification);
                    details.add(newDetails);
                }
            }
            detailsRepository.saveAll(details);
        }

        return new CustomResponse(
                200,
                "Certification updated successfully",
                HttpStatus.OK
        );
    }

    public CustomResponse updateCertificationDetail(
            Long id, @Valid UpdateDetailRequest request) throws ResourceNotFound {
        CertificationDetails detail = detailsRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Certification Detail not found with id"));

        detail.setDetails(request.getDetail());

        detailsRepository.save(detail);

        var message = "Certification detail Successfully updated";

        return new CustomResponse(200, message, HttpStatus.OK);
    }

    @Transactional
    public CustomResponse deleteCertification(Long id) throws ResourceNotFound {
        Certification certification = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Certification not found with id"));

        repository.delete(certification);

        var message = "Certification deleted successfully";
        return new CustomResponse(200, message, HttpStatus.OK);
    }
}

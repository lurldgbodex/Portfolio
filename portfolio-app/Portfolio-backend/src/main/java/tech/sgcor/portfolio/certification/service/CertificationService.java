package tech.sgcor.portfolio.certification.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.certification.entity.CertificationDetails;
import tech.sgcor.portfolio.certification.dto.CertificationDto;
import tech.sgcor.portfolio.certification.dto.UpdateCertification;
import tech.sgcor.portfolio.certification.dto.UpdateDetailRequest;
import tech.sgcor.portfolio.certification.entity.Certification;
import tech.sgcor.portfolio.certification.repository.CertificationDetailsRepository;
import tech.sgcor.portfolio.certification.repository.CertificationRepository;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final CertificationRepository repository;
    private final CertificationDetailsRepository detailsRepository;

    public List<Certification> getCertifications(Long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public Certification addCertification(CertificationDto request) {
        Certification certification = new Certification();
        certification.setName(request.getName());
        certification.setBody(request.getBody());
        certification.setDate(LocalDate.parse(request.getDate()));
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
            Long id, UpdateCertification request) {
        Certification certification = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("una certificate with id " + id + " no dey"));

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getBody(), ""),
                Objects.toString(request.getDate(), ""),
                Objects.toString(request.getDetails(), "")
        ).allMatch(StringUtils::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("wetin u wan update?");
        }

        certification.setName(SharedService.isNotBlank(request.getName()) ?
                request.getName() : certification.getName());
        certification.setBody(SharedService.isNotBlank(request.getBody()) ?
                request.getBody() : certification.getBody());
        certification.setDate(request.getDate() != null
                ? LocalDate.parse(request.getDate()) : certification.getDate());

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
                "una certificate update dey successful",
                HttpStatus.OK
        );
    }

    public CustomResponse updateCertificationDetail(
            Long id, UpdateDetailRequest request) throws ResourceNotFound {
        CertificationDetails detail = detailsRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("una certificate detail with id " + id + " no dey"));

        detail.setDetails(request.getDetail());

        detailsRepository.save(detail);

        var message = "una certificate update dey successful";

        return new CustomResponse(200, message, HttpStatus.OK);
    }

    @Transactional
    public CustomResponse deleteCertification(Long id) throws ResourceNotFound {
        Certification certification = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("una certificate with id " + id + " no dey"));

        repository.delete(certification);

        var message = "una don comot the certificate";
        return new CustomResponse(200, message, HttpStatus.OK);
    }
}

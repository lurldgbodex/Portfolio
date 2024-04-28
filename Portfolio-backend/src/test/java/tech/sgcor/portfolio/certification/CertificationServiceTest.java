package tech.sgcor.portfolio.certification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificationServiceTest {
    @InjectMocks
    private CertificationService underTest;
    @Mock
    private CertificationRepository certificationRepository;

    @Test
    void getCertificationSuccessTest() {
        var certification = new Certification();
        certification.setName("ALX");

        List<Certification> certificationList = new ArrayList<>();
        certificationList.add(certification);

        when(certificationRepository.findByUserId(10L)).thenReturn(certificationList);

        var res = underTest.getCertifications(10L);

        assertThat(res).isNotEmpty();
        assertThat(res).contains(certification);
    }

    @Test
    void addCertificationSuccessTest() {
        CertificationDto request = new CertificationDto();
        request.setBody("Oracle");
        request.setName("Software-Engineer");
        request.setUser_id(4L);
        List<String> details = new ArrayList<>();
        details.add("detail1");
        request.setDetails(details);
        request.setDate("2022-02-03");

        Certification expected = new Certification();
        expected.setId(1L);
        expected.setBody(request.getBody());
        expected.setName(request.getName());
        expected.setUserId(request.getUser_id());
        List<CertificationDetails> certDetails = request.getDetails().stream().map(dit -> {
           CertificationDetails detail = new CertificationDetails();
           detail.setDetails(dit);
            return detail;
        }).toList();

        expected.setDetails(certDetails);

        when(certificationRepository.save(any(Certification.class))).thenReturn(expected);

        var res = underTest.addCertification(request);
        assertThat(res.getBody()).isEqualTo(expected.getBody());
        assertThat(res.getId()).isEqualTo(expected.getId());

        verify(certificationRepository, times(1)).save(any(Certification.class));
    }


}
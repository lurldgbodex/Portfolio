package tech.sgcor.portfolio.about;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AboutServiceTest {
    @Mock
    private AboutRepository aboutRepository;
    @InjectMocks
    private AboutService underTest;

    @Test
    void getAboutSuccessTest() throws ResourceNotFound {
        Long id = 40L;
        var about = new About();
        about.setName("test");
        about.setId(id);
        about.setEmail("test-mail");
        about.setDob(LocalDate.now());

        given(aboutRepository.findById(id)).willReturn(Optional.of(about));

        var req = underTest.getAbout(id);

        verify(aboutRepository, times(1)).findById(id);
        assertThat(req.name()).isEqualTo(about.getName());
        assertThat(req.id()).isEqualTo(about.getId());
        assertThat(req.email()).isEqualTo(about.getEmail());
        assertThat(req.socials()).containsKeys("github", "linkedin", "medium");
    }

    @Test
    void getAboutNotFoundTest() {
        Long id = 8L;

        given(aboutRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getAbout(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Data not found with id " + id);

        verify(aboutRepository, times(1)).findById(id);
    }

    @Test
    void addSuccessTest() {
        var request = new CreateRequest();

        request.setName("test-name");
        request.setTitle("test-title");
        request.setAddress("test-address");
        request.setPhone_number("+2340011223344");

        var res = underTest.add(request);

        var about = new About();
        about.setName(request.getName());
        about.setTitle(request.getTitle());
        about.setAddress(request.getAddress());
        about.setPhoneNumber(request.getPhone_number());

        verify(aboutRepository, times(1)).save(about);

        assertThat(res).isInstanceOf(CustomResponse.class);
        assertThat(res.code()).isEqualTo(201);
        assertThat(res.message()).isEqualTo("about added successfully");
        assertThat(res.status()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateSuccessTest() throws ResourceNotFound {
        Long id = 10L;
        var about = new About();

        var req = new UpdateRequest();
        req.setAddress("new_address");


        given(aboutRepository.findById(id)).willReturn(Optional.of(about));

        var res = underTest.update(id, req);

        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("update successful");

        verify(aboutRepository, times(1)).findById(id);
    }

    @Test
    void updateNotFoundTest() {
        Long id = 2L;
        var req = new UpdateRequest();
        req.setName("update-test");

        given(aboutRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.update(id, req))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Data not found with id " + id);
    }

    @Test
    void deleteSuccessTest() throws ResourceNotFound {
        Long id = 1L;

        given(aboutRepository.findById(id)).willReturn(Optional.of(new About()));

        var res = underTest.delete(id);

        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("about successfully deleted with id " + id);
        assertThat(res.status()).isEqualTo(HttpStatus.OK);

        verify(aboutRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteNotFoundTest() {
        Long id = 2L;
        given(aboutRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.delete(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Data not found with id " + id);
    }
}
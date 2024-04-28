package tech.sgcor.portfolio.about;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.user.User;

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
        User user = new User();
        user.setFirstName("segun");
        user.setLastName("gbodi");
        user.setId(8L);
        Long id = 40L;

        var about = new About();
        about.setUser(user);
        about.setId(id);
        about.setEmail("test-mail");
        about.setDob(LocalDate.now());

        given(aboutRepository.findAboutWithUserById(user.getId())).willReturn(Optional.of(about));

        var req = underTest.getAbout(user.getId());

        verify(aboutRepository, times(1)).findAboutWithUserById(user.getId());

        assertThat(req.getId()).isEqualTo(about.getId());
        assertThat(req.getEmail()).isEqualTo(about.getEmail());
        assertThat(req.getDob()).isEqualTo(about.getDob());
        assertThat(req.getUser().getFirstName()).isEqualTo(user.getFirstName());
        assertThat(req.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void getAboutNotFoundTest() {
        Long id = 8L;

        given(aboutRepository.findAboutWithUserById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getAbout(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("about no dey for user with id " + id);

        verify(aboutRepository, times(1)).findAboutWithUserById(id);
    }

    @Test
    void addSuccessTest() {
        var request = new CreateRequest();

        User user = new User();
        user.setFirstName("Idan");

        request.setTitle("test-title");
        request.setAddress("test-address");
        request.setPhone_number("+2340011223344");
        request.setFirst_name("Idan");
        request.setDob("1999-10-19");

        var about = new About();
        about.setTitle(request.getTitle());
        about.setAddress(request.getAddress());
        about.setPhoneNumber(request.getPhone_number());
        about.setDob(LocalDate.parse(request.getDob()));
        about.setUser(user);

        given(aboutRepository.save(about)).willReturn(about);

        var res = underTest.add(request);


        verify(aboutRepository, times(1)).save(about);

        assertThat(res).isInstanceOf(About.class);
    }

    @Test
    void partialUpdateSuccessTest() {
        Long userId = 10L;
        User user = new User();
        user.setLastName("Apata");
        user.setId(userId);

        var about = new About();
        about.setUser(user);
        about.setTitle("Bad-Stopper");

        var req = new UpdateRequest();
        req.setAddress("new_address");
        req.setFirst_name("Ijaya");


        given(aboutRepository.findAboutWithUserById(userId)).willReturn(Optional.of(about));

        var res = underTest.update(userId, req);

        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("una update dey successful");

        verify(aboutRepository, times(1)).findAboutWithUserById(userId);
        verify(aboutRepository, times(1)).save(any(About.class));
    }

    @Test
    void fullUpdateSuccessTest() {
        Long userId = 10L;
        User user = new User();
        user.setLastName("Apata");
        user.setId(userId);

        var about = new About();
        about.setUser(user);
        about.setTitle("Bad-Stopper");

        var req = new UpdateRequest();
        req.setAddress("new_address");
        req.setFirst_name("Ijaya");
        req.setLast_name("Hmm");
        req.setDob(LocalDate.now().toString());
        req.setEmail("david@mail.com");
        req.setTitle("Enforcer");
        req.setGithub("https://github.com/ijaya-apata");
        req.setLinkedin("https://linkedin.com/in/ijaya-apata");
        req.setMedium("https://medium.com/ijaya-apata");
        req.setMiddle_name("Piti");
        req.setPhone_number("+223889873");
        req.setSummary("Ijaya omo eniyan. Apatapiti Akoni.");


        given(aboutRepository.findAboutWithUserById(userId)).willReturn(Optional.of(about));

        var res = underTest.update(userId, req);

        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("una update dey successful");

        verify(aboutRepository, times(1)).findAboutWithUserById(userId);
        verify(aboutRepository, times(1)).save(any(About.class));
    }

    @Test
    void updateNotFoundTest() {
        Long id = 2L;
        var req = new UpdateRequest();

        given(aboutRepository.findAboutWithUserById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.update(id, req))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("about no dey for user with id " + id);
    }

    @Test
    void updateNoData() {
        Long userId = 2L;
        var req = new UpdateRequest();

        given(aboutRepository.findAboutWithUserById(userId)).willReturn(Optional.of(new About()));

        assertThatThrownBy(() -> underTest.update(userId, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("wetin u wan update?");
    }

    @Test
    void deleteSuccessTest() {
        Long userId = 1L;
        var about = About.builder().build();

        given(aboutRepository.findAboutWithUserById(userId)).willReturn(Optional.of(about));

        var res = underTest.delete(userId);

        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("una don successfully delete about for user wey get the id " + userId);
        assertThat(res.status()).isEqualTo(HttpStatus.OK);

        verify(aboutRepository, times(1)).delete(about);
    }
}
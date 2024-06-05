package tech.sgcor.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tech.sgcor.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    private User user;
    @Autowired
    private UserRepository underTest;

    @BeforeEach
    void setup () {
        user = new User();
        user.setEmail("test@mail.com");
        user.setFirstName("first-name");
        user.setLastName("last-name");
        user.setId(5L);
        user.setConfirmationToken("confirm-token");
        underTest.save(user);
    }

    @AfterEach
    void tearDown() {
        underTest.delete(user);
    }

    @Test
    void shouldFindByEmail() {
       var res = underTest.findByEmail(user.getEmail());
       assertThat(res).isPresent();
       assertThat(res).contains(user);

       // should return null if user email not found
        res = underTest.findByEmail("invalid@mail.com");
        assertThat(res).isEmpty();
    }

    @Test
    void findByConfirmationToken() {
        var res = underTest.findByConfirmationToken(user.getConfirmationToken());
        assertThat(res).isPresent();
        assertThat(res).contains(user);

        // should return null if user not found with confirmation token
        res = underTest.findByConfirmationToken("invalid-token");
        assertThat(res).isEmpty();
    }
}
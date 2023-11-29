package com.sgcor.shopply.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepoTest;

    @AfterEach
    void tearDown() {
        userRepoTest.deleteAll();
    }

    @Test
    void itShouldFindByUsernameIfExist() {
        User user = new User();
        String username = "newUser";
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("newuser@password.com");
        userRepoTest.save(user);

        Optional<User> expected = userRepoTest.findByUsername(username);

        assertThat(expected).contains(user);
    }

    @Test
    void itShouldNotFindByUsernameNotExist() {
        String username = "newUser";
        Optional<User> expected = userRepoTest.findByUsername(username);

        assertThat(expected).isEmpty();
    }

    @Test
    void itShouldFindByEmailIfExist() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");
        String email = "newuser@password.com";
        user.setEmail(email);
        userRepoTest.save(user);

        User expected = userRepoTest.findByEmail(email);

        assertThat(expected).isEqualTo(user);
    }

    @Test
    void itShouldNotFindByEmailIfNotExist() {

        String email = "newuser@password.com";
        User expected = userRepoTest.findByEmail(email);

        assertThat(expected).isEqualTo(null);
    }

    @Test
    void itShouldFindByConfirmationTokenIfExist() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");
        user.setEmail("newuser@password.com");
        String confirmToken = "2983y539582e";
        user.setConfirmationToken(confirmToken);
        userRepoTest.save(user);

        User expected = userRepoTest.findByConfirmationToken(confirmToken);

        assertThat(expected).isEqualTo(user);
    }

    @Test
    void itShouldNotFindByConfirmationTokenIfNotExist() {

        String token = "2938957203come38";
        User expected = userRepoTest.findByConfirmationToken(token);

        assertThat(expected).isEqualTo(null);
    }
}
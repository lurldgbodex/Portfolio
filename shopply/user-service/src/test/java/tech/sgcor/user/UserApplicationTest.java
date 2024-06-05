package tech.sgcor.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import tech.sgcor.sharedservice.dto.CustomResponse;
import tech.sgcor.user.dto.CreateUserRequest;
import tech.sgcor.user.dto.UserDto;
import tech.sgcor.user.model.User;
import tech.sgcor.user.repository.UserRepository;
import tech.sgcor.user.service.UserService;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApplicationTest {

    @LocalServerPort
    private int port;
    private final String baseUrl = "http://localhost:" + port + "/api/users";
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = new User();

        user.setEmail("test@user.com");
        user.setPassword("LetMeInh@h@h@");
        user.setFirstName("test");
        user.setLastName("user");

        userRepository.save(user);
    }

    @Test
    void shouldCreateAnewUserTest() {
        String url = baseUrl + "/create";

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test-user@email.com");
        request.setFirst_name("test1");
        request.setLast_name("user1");
        request.setPassword("Password6.6.6.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateUserRequest> userRequest = new HttpEntity<>(request, headers);


        var res = testRestTemplate.exchange(
                url, HttpMethod.POST, userRequest, String.class);

        var body = res.getBody();
        URI location = res.getHeaders().getLocation();

        assert body != null;
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(body.code()).isEqualTo(201);
//        assertThat(body.message()).isEqualTo("user created successfully");

        assertThat(location).isNotNull();
        assertThat(location).hasFragment(baseUrl + "/");
    }

    @Test
    void shouldGetUserDetailsByEmail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("test@email.com", headers);

        ResponseEntity<UserDto> user = testRestTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, UserDto.class);

        UserDto res = user.getBody();

        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

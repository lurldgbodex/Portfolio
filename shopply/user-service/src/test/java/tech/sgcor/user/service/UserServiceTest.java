package tech.sgcor.user.service;

import com.github.dockerjava.api.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.sgcor.sharedservice.dto.CustomResponse;
import tech.sgcor.user.dto.CreateUserRequest;
import tech.sgcor.user.dto.GetRequest;
import tech.sgcor.user.dto.UpdateUserDetails;
import tech.sgcor.user.exception.InvalidTokenException;
import tech.sgcor.user.exception.UserExistsException;
import tech.sgcor.user.exception.UserNotFoundException;
import tech.sgcor.user.model.User;
import tech.sgcor.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private User user;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService underTest;

    @BeforeEach
    void setup() {

        String email = "test@email.com";
        user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
    }

    @Test
    void findByEmail() {
        /**
         * successfully find valid user by email
         */
        String email = "test@email.com";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var res = underTest.findByEmail(email);

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo(email);
        assertThat(res.getFirstName()).isEqualTo("Test");
        assertThat(res.getLastName()).isEqualTo("User");

        /**
         * throw a error if user is does not exist
         */
        assertThatThrownBy(() -> underTest.findByEmail("invalid@user.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("user with email not found");
    }

    @Test
    void registerUser() {
        /**
         * when user with email already exist
         */
        String email = "test@email.com";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(email);

        assertThatThrownBy(() -> underTest.registerUser(request))
                .isInstanceOf(UserExistsException.class)
                .hasMessageContaining("user already exists for this email");

        /**
         * successfully create a user
         */
        request.setEmail("new@user.com");
        request.setFirst_name("New");
        request.setLast_name("user");
        request.setOther_name("others");

        var res = underTest.registerUser(request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getConfirmationToken()).isNotNull();

        assertThat(res).isNotNull();
        assertThat(res.body()).isEqualTo(new CustomResponse(201, "user created successfully"));
    }

    @Test
    void getUserDetails() {
        // find user by email
        when(userRepository.findByEmail(this.user.getEmail())).thenReturn(Optional.of(user));

        GetRequest request = new GetRequest();
        request.setEmail("test@email.com");
        var res = underTest.getUserDetails(request);

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo("test@email.com");
        assertThat(res.getFirst_name()).isEqualTo("Test");
        assertThat(res.getLast_name()).isEqualTo("User");
        assertThat(res.getId()).isEqualTo(1);
        verify(userRepository, times(1)).findByEmail("test@email.com");

        // find user by id
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Long userId = 1L;
        var byId = underTest.getUserById(userId);

        assertThat(byId).isNotNull();
        assertThat(byId.getEmail()).isEqualTo("test@email.com");
        assertThat(byId.getId()).isEqualTo(1);
        verify(userRepository, times(1)).findById(1L);

        // find user with invalid id
        assertThatThrownBy(() -> underTest.getUserById(10L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("user not found with id");
    }

    @Test
    void updateUserDetails() {
        // update with empty request body
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UpdateUserDetails request = new UpdateUserDetails();
        request.setEmail(user.getEmail());

        assertThatThrownBy(() -> underTest.updateUserDetails(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("provide the field you want to update");

        // successfully update user
        request.setOther_name("others");
        CustomResponse response = underTest.updateUserDetails(request);

        verify(userRepository, times(2)).findByEmail(user.getEmail());
        verify(userRepository).saveAndFlush(any(User.class));

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("user details updated successfully");

    }

    @Test
    void confirmAccountTest() {
        /**
         * test with invalid token value
         */
        String confirmationToken = "valid-confirm-token";
        user.setConfirmationToken(confirmationToken);

        String token = "invalid token";

        assertThatThrownBy(() -> underTest.confirmAccount(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid confirmation token");

        /**
         * successfully confirm account
         */
        when(userRepository.findByConfirmationToken(confirmationToken)).thenReturn(Optional.of(user));
        var res = underTest.confirmAccount(confirmationToken);
        assertThat(res).isNotNull();
        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("Account successfully confirmed");

        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getConfirmationToken()).isNullOrEmpty();
    }
}
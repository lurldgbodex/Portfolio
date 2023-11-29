package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.config.JwtService;
import com.sgcor.shopply.message.EmailMessage;
import com.sgcor.shopply.message.EmailService;
import com.sgcor.shopply.shared.SharedService;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.User;
import com.sgcor.shopply.user.UserRepository;
import com.sgcor.shopply.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @Mock
    private SharedService sharedService;

    @InjectMocks
    private AuthService underTest;

    @Test
    void registerUserSuccess() throws Exception {
        AuthDTO authDTO = new AuthDTO();
        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";

        authDTO.setUsername(username);
        authDTO.setPassword(password);
        authDTO.setEmail(email);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(sharedService.isNotValidPassword(password)).thenReturn(false);
        when(userService.isValidEmail(email)).thenReturn(true);

        String token = "randomGeneratedToken";
        when(jwtService.generateToken(any())).thenReturn(token);

        assertThatNoException().isThrownBy(() -> {
            String result = underTest.registerUser(authDTO);
            assertThat(result).isEqualTo(token);

        });

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));

    }

    @Test
    void registerUserWithFullDetails() throws Exception {
        
        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";
        LocalDate dateOfBirth = LocalDate.now();
        String firstName = "John";
        String lastName = "Doe";
        String address = "123 Main St";
        String phoneNumber = "123456789";
        String image = "new-image-url";

        AuthDTO authDTO = new AuthDTO(
                username, 
                password, 
                firstName, 
                lastName, 
                email, 
                address, 
                phoneNumber, 
                image, 
                dateOfBirth
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(sharedService.isNotValidPassword(password)).thenReturn(false);
        when(userService.isValidEmail(email)).thenReturn(true);

        String token = "randomGeneratedToken";
        when(jwtService.generateToken(any())).thenReturn(token);

        assertThatNoException().isThrownBy(() -> {
            String result = underTest.registerUser(authDTO);
            assertThat(result).isEqualTo(token);

        });

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));

    }

    @Test
    void registerUserWithUsernameAndPasswordRequired() {
        AuthDTO authDTO = new AuthDTO();

        assertThatThrownBy(()-> underTest.registerUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("username, email and password is required");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void registerUserWithExistingUsername() {
        AuthDTO authDTO = new AuthDTO();

        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";

        authDTO.setUsername(username);
        authDTO.setPassword(password);
        authDTO.setEmail(email);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(()-> underTest.registerUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User with username already exist");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void registerUserWithExistingEmail() {
        AuthDTO authDTO = new AuthDTO();

        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";

        authDTO.setUsername(username);
        authDTO.setPassword(password);
        authDTO.setEmail(email);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(new User());

        assertThatThrownBy(()-> underTest.registerUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User with email already exist");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void registerUserWithInvalidPassword() {
        AuthDTO authDTO = new AuthDTO();

        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";

        authDTO.setUsername(username);
        authDTO.setPassword(password);
        authDTO.setEmail(email);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(sharedService.isNotValidPassword(password)).thenReturn(true);

        assertThatThrownBy(()-> underTest.registerUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("password must be minimum 6 characters, with an uppercase, lowercase and special character");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void registerUserWithInvalidEmail() {
        AuthDTO authDTO = new AuthDTO();

        String username = "newUser";
        String password = "password";
        String email = "newUser@example.com";

        authDTO.setUsername(username);
        authDTO.setPassword(password);
        authDTO.setEmail(email);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(sharedService.isNotValidPassword(password)).thenReturn(false);
        when(userService.isValidEmail(email)).thenReturn(false);

        assertThatThrownBy(()-> underTest.registerUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid email. Please provide a valid Email");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void loginUserSuccessfully() throws UnauthorizedException, BadRequestException {
        AuthDTO authDTO = new AuthDTO();

        User user = new User();

        String username = "someUser";
        String password = "password";

        authDTO.setUsername(username);
        authDTO.setPassword(password);

        user.setUsername(username);
        user.setPassword("passwordHash");

        String generatedToken = "randomGeneratedToken";

        when(userService.loadUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn(generatedToken);

        String result = underTest.loginUser(authDTO);
        assertThat(result).isEqualTo(generatedToken);
    }

    @Test
    void loginUserWithUsernameAndPasswordRequired() {
        AuthDTO authDTO = new AuthDTO();

        assertThatThrownBy(()-> underTest.loginUser(authDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("username and password is required");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmail(any(EmailMessage.class));
    }

    @Test
    void loginUserWithNotFoundUsername() {
        AuthDTO authDTO = new AuthDTO();

        String username = "someUser";
        String password = "password";

        authDTO.setUsername(username);
        authDTO.setPassword(password);

        assertThatThrownBy(() -> underTest.loginUser(authDTO))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void loginUserWithWrongPassword() {
        AuthDTO authDTO = new AuthDTO();

        String username = "someUser";
        String password = "password";

        authDTO.setUsername(username);
        authDTO.setPassword(password);

        User userDetails = new User();
        userDetails.setUsername(username);
        userDetails.setPassword("passwordHash");

        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(false);
        assertThatThrownBy(() -> underTest.loginUser(authDTO))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Authentication failed");
    }
}
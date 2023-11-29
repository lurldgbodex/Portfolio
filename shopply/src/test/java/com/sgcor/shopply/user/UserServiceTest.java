package com.sgcor.shopply.user;

import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.SharedService;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.auth.PasswordChangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SharedService sharedService;
    @InjectMocks
    private UserService underTest;

    @BeforeEach
    void setUp() throws UnauthorizedException {
        // Mock an authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");

        // Set up securityContext with authenticated user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String currentUser = underTest.findCurrentUser();
    }


    @Test
    void canLoadUserByUsername() {
        User user = new User();
        String username = "newUser";
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("newuser@password.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = underTest.loadUserByUsername(username);

        verify(userRepository).findByUsername(username);
        assertThat(userDetails).isEqualTo(user);
    }

    @Test
    void loadUserByUsernameWillThrowExceptionIfNoUser() {
        User user = new User();
        String username = "newUser";
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("newuser@password.com");


        assertThatThrownBy(()->underTest.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username: " + username);
    }

    @Test
    void canSaveUser() {
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setPassword("testPassword");
        newUser.setEmail("testuser@sample.com");

        underTest.save(newUser);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(newUser);
    }

    @Test
    void canFindCurrentUserWhenAuthenticated() throws Exception {
        String currentUser = underTest.findCurrentUser();
        assertThat(currentUser).isEqualTo("testUser");
    }

    @Test
    void findCurrentUserThrowsExceptionWhenNotAuthenticated() {
        // Mock a not authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Set up securityContext with the unauthenticated user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> underTest.findCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("no current user");
    }

    @Test
    void isValidEmailReturnsTrueForValidEmail() {
        assertThat(underTest.isValidEmail("user@sample.com")).isTrue();
        assertThat(underTest.isValidEmail("user.name@example.co")).isTrue();
        assertThat(underTest.isValidEmail("user123@subdomain.example.org")).isTrue();
    }

    @Test
    void isValidEmailReturnsFalseForInvalidEmail() {
        assertThat(underTest.isValidEmail(null)).isFalse();
        assertThat(underTest.isValidEmail("")).isFalse();
        assertThat(underTest.isValidEmail("user@.com")).isFalse();
        assertThat(underTest.isValidEmail("user@com")).isFalse();
        assertThat(underTest.isValidEmail("user@com.")).isFalse();
        assertThat(underTest.isValidEmail("user@.com")).isFalse();
    }

    @Test
    void canUpdateUser() throws BadRequestException, UnauthorizedException {
        // Mock current user
        User currentUser = new User();
        String username = "testUser";
        currentUser.setUsername(username);
        currentUser.setEmail("testuser@example.com");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        // Mock userRepository findByEmail
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Mock userRepository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // create a request to update user
        UserDetailDTO request = new UserDetailDTO();
        String email = "newuser@example.com";
        request.setEmail(email);
        request.setDateOfBirth("2000-01-01");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("123 Main St");
        request.setPhoneNumber("123456789");
        request.setImage("new-image-url");

        // call the updateUser method
        GenericResponse response = underTest.updateUser(request);

        // verify userRepository findByEmail and save were called
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(any(User.class));

        // verify the response
        assertThat(response.message()).isEqualTo("User updated successfully");
    }

    @Test
    void updateUserThrowsExceptionForExistingEmail() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setEmail("testuser@example.com");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        // Mock UserRepository findByEmail to return a non-empty result
        when(userRepository.findByEmail(anyString())).thenReturn(new User());

        // Create a request with an email that already exists
        UserDetailDTO request = new UserDetailDTO();
        request.setEmail("existing-email@example.com");

        // Call the updateUser method and expect a RuntimeException
        assertThatThrownBy(() -> underTest.updateUser(request))
                .hasMessageContaining("User with email already exists")
                .isInstanceOf(BadRequestException.class);

        // Verify that UserRepository findByEmail and save was called
        verify(userRepository).findByEmail("existing-email@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserThrowsExceptionForInvalidEmail() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setEmail("testuser@example.com");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        // Create a request with an invalid email
        UserDetailDTO request = new UserDetailDTO();
        request.setEmail("invalid-email");

        // Call the updateUser method and expect an exception
        assertThatThrownBy( () -> underTest.updateUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Please provide a valid email");

        // Verify that UserRepository findByEmail and save were not called
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserThrowsExceptionForInvalidDateOfBirthFormat() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setEmail("testuser@example.com");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        // Create a request with an invalid date of birth format
        UserDetailDTO request = new UserDetailDTO();
        request.setDateOfBirth("invalid-date");

        // Call the updateUser method and expect an exception
        assertThatThrownBy(() -> underTest.updateUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid date format: " + "yyyy-mm-dd");

        // Verify that UserRepository findByEmail and save were not called
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserShouldThrowExceptionIfUserNotFound() {
        // Mock not found user
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // create a request to update user
        UserDetailDTO request = new UserDetailDTO();
        String email = "newuser@example.com";
        request.setEmail(email);
        request.setDateOfBirth("2000-01-01");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("123 Main St");
        request.setPhoneNumber("123456789");
        request.setImage("new-image-url");

        assertThatThrownBy(() -> underTest.updateUser(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found with username: " + username);
    }


    @Test
    void canGetUserDetails() throws Exception {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");
        currentUser.setEmail("testuser@example.com");
        currentUser.setAddress("123 Main St");
        currentUser.setDateOfBirth(LocalDate.parse("1990-01-01"));
        currentUser.setPhoneNumber("123456789");
        currentUser.setImage("user-image");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));


        // Call the getUserDetails method
        UserDetailDTO result = underTest.getUserDetails();

        // Verify that the returned UserDetailDTO has the expected values
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat("Doe").isEqualTo(result.getLastName());
        assertThat("testuser@example.com").isEqualTo(result.getEmail());
        assertThat("123 Main St").isEqualTo(result.getAddress());
        assertThat("1990-01-01").isEqualTo(result.getDateOfBirth());
        assertThat("123456789").isEqualTo(result.getPhoneNumber());
        assertThat("user-image").isEqualTo(result.getImage());
    }

    @Test
    void confirmUserEmailSuccessfully() throws BadRequestException {
        // Mock current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Mock user with a valid confirmation token
        User user = new User();
        user.setConfirmationToken("validToken");
        user.setConfirmationTokenExpiration(currentDateTime.plusHours(1));  // Set expiration to 1 hour from now
        when(userRepository.findByConfirmationToken("validToken")).thenReturn(user);

        // Mock UserRepository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the confirmUserEmail method
        GenericResponse response = underTest.confirmUserEmail("validToken");

        // Verify that UserRepository.findByConfirmationToken and save were called
        verify(userRepository).findByConfirmationToken("validToken");
        verify(userRepository).save(any(User.class));

        // Verify the response
        assertThat("User confirmed successfully").isEqualTo(response.message());
    }

    @Test
    void confirmUserEmailWithInvalidToken() {
        // Mock user with an invalid confirmation token
        when(userRepository.findByConfirmationToken("invalidToken")).thenReturn(null);

        // Call the confirmUserEmail method and expect a BadRequestException
        assertThatThrownBy(() -> underTest.confirmUserEmail("invalidToken"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid Confirmation Token");

        // Verify that UserRepository.findByConfirmationToken was called
        verify(userRepository).findByConfirmationToken("invalidToken");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void confirmUserEmailWithExpiredToken() {
        // Mock current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Mock user with an expired confirmation token
        User user = new User();
        user.setConfirmationToken("expiredToken");
        user.setConfirmationTokenExpiration(currentDateTime.minusHours(1));  // Set expiration to 1 hour ago
        when(userRepository.findByConfirmationToken("expiredToken")).thenReturn(user);

        // Call the confirmUserEmail method and expect a BadRequestException
        assertThatThrownBy(() -> underTest.confirmUserEmail("expiredToken"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Expired confirmation Token");

        // Verify that UserRepository.findByConfirmationToken was called
        verify(userRepository).findByConfirmationToken("expiredToken");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordSuccessfully() throws BadRequestException {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword("currentPasswordHash");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));

        // Mock PasswordChangeRequest with valid data
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        // Mock passwordEncoder matches
        when(passwordEncoder.matches("currentPassword", "currentPasswordHash")).thenReturn(true);

        // Mock sharedService isValidPassword
        when(sharedService.isNotValidPassword("newPassword")).thenReturn(false);

        // Mock UserRepository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the changePassword method
        assertThatNoException().isThrownBy(() -> {
            GenericResponse response = underTest.changePassword(request);
            assertThat(response.message()).isEqualTo("password updated successfully");
        });

        // Verify that UserRepository.save was called
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePasswordMissingPasswords() {
        // Mock PasswordChangeRequest with missing passwords
        PasswordChangeRequest request = new PasswordChangeRequest();

        // Call the changePassword method and expect a BadRequestException
        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You need to provide your current password and new password");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordIncorrectCurrentPassword() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword("currentPasswordHash");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));

        // Mock PasswordChangeRequest with incorrect current password
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("incorrectCurrentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        // Mock passwordEncoder does not match
        when(passwordEncoder.matches("incorrectCurrentPassword", "currentPasswordHash")).thenReturn(false);

        // Call the changePassword method and expect an IllegalStateException
        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Wrong password");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordInvalidNewPassword() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword("currentPasswordHash");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));

        // Mock PasswordChangeRequest with invalid new password
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("weakPassword");
        request.setConfirmPassword("weakPassword");

        // Mock passwordEncoder matches
        when(passwordEncoder.matches("currentPassword", "currentPasswordHash")).thenReturn(true);

        // Mock sharedService isNotValidPassword
        when(sharedService.isNotValidPassword("weakPassword")).thenReturn(true);

        // Call the changePassword method and expect an IllegalStateException
        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("password must be minimum 6 characters, with an uppercase, lowercase and special character");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordPasswordsDoNotMatch() {
        // Mock current user
        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword("currentPasswordHash");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));

        // Mock PasswordChangeRequest with non-matching new and confirm passwords
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("differentPassword");

        // Mock passwordEncoder matches
        when(passwordEncoder.matches("currentPassword", "currentPasswordHash")).thenReturn(true);

        // Call the changePassword method and expect an IllegalStateException
        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Password do not match");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordShouldThrowExceptionIfNoAuthenticatedUser() {
        // Mock a not authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Set up securityContext with the unauthenticated user
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock PasswordChangeRequest with valid data
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        PasswordChangeRequest request = new PasswordChangeRequest(
                currentPassword, newPassword, newPassword
        );


        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no current user");

        // Verify that UserRepository.save was not called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordShouldThrowExceptionIfNotUser() {
        // Mock not found user
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Mock PasswordChangeRequest with valid data
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        assertThatThrownBy(() -> underTest.changePassword(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found with username: " + username);
    }
}
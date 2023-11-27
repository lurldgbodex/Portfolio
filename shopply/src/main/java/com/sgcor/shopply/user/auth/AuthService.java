package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.config.JwtService;
import com.sgcor.shopply.message.EmailMessage;
import com.sgcor.shopply.message.EmailService;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.Role;
import com.sgcor.shopply.user.User;
import com.sgcor.shopply.user.UserRepository;
import com.sgcor.shopply.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    public final UserService userService;
    public final JwtService jwtService;
    public final AuthenticationManager authenticationManager;
    public final EmailService emailService;

    public String registerUser(AuthDTO authDTO ) throws Exception {
        User newUser = new User();
        String confirmationToken = UUID.randomUUID().toString();

        Optional<User> user = userRepository.findByUsername(authDTO.getUsername());
        User emailExist = userRepository.findByEmail(authDTO.getEmail());

        if (authDTO.getUsername() == null || authDTO.getPassword() == null) {
            throw new BadRequestException("username and password is required");
        }

        if (user.isPresent()) {
            throw new Exception("User with username already");
        }

        if (emailExist != null) {
            throw new Exception("User with email already exist");
        }

        if (isNotValidPassword(authDTO.getPassword())) {
            throw new BadRequestException("password must be minimum 6 characters, with an uppercase, lowercase and special character");
        }

        if (authDTO.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        if (!userService.isValidEmail(authDTO.getEmail())) {
            throw new BadRequestException("Please provide a valid Email");
        }
        newUser.setConfirmationToken(confirmationToken);
        newUser.setUsername(authDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        newUser.setEmail(authDTO.getEmail());

        newUser.setFirstName(authDTO.getFirstName() != null ? authDTO.getFirstName() : null);
        newUser.setLastName(authDTO.getLastName() != null ? authDTO.getLastName() : null);
        newUser.setAddress(authDTO.getAddress() != null ? authDTO.getAddress() : null);
        newUser.setPhoneNumber(authDTO.getPhoneNumber() != null ? authDTO.getPhoneNumber() : null);
        newUser.setImage(authDTO.getImage() != null ? authDTO.getImage() : null);
        newUser.setRole(Role.USER);

        LocalDateTime expirationTime = LocalDateTime.now().plusHours(24);
        newUser.setConfirmationTokenExpiration(expirationTime);

        userRepository.save(newUser);

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(authDTO.getEmail());
        emailMessage.setSubject("Verify your registration");
        String emailContent = "Dear " + authDTO.getUsername() + "\n\nplease click the link below to verify your registration\n\n";
        emailContent += "verification code: " + confirmationToken + "\n\n";
        emailContent += "Thank you\n The Shopply Team";
        emailMessage.setBody(emailContent);
        sendConfirmationEmail(emailMessage);


        return jwtService.generateToken(newUser);
    }

    private void sendConfirmationEmail(EmailMessage emailMessage) {
        emailService.sendEmail(emailMessage);
    }

    public String loginUser(AuthDTO authDTO) throws BadRequestException, UnauthorizedException, UsernameNotFoundException {
        if (authDTO.getUsername() == null || authDTO.getPassword() == null) {
            throw new BadRequestException("Username and Password is required");
        }
        UserDetails userDetails = userService.loadUserByUsername(authDTO.getUsername());
        if (passwordEncoder.matches(authDTO.getPassword(), userDetails.getPassword())) {
            return jwtService.generateToken(userDetails);
        } else {
            throw new UnauthorizedException("Authentication failed");
        }
    }

    public void changePassword(PasswordChangeRequest request) {
        try {
            User user = (User) userService.loadUserByUsername(userService.findCurrentUser());
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
                throw new IllegalStateException("Wrong password");
            }
            if (isNotValidPassword(request.getNewPassword())) {
                throw new IllegalStateException("password must be minimum 6 characters, with an uppercase, lowercase and special character");
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalStateException("Password do not match");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    public boolean isNotValidPassword(String password) {
        if (password.length() < 6) {
            return true;
        }

        // Check for at least one special character
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (!specialCharMatcher.find()) {
            return true;
        }

        // Check for at least one uppercase character
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        if (!uppercaseMatcher.find()) {
            return true;
        }

        // Check for at least one lowercase character
        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);
        return !lowercaseMatcher.find();
    }
}

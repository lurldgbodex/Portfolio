package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.config.JwtService;
import com.sgcor.shopply.message.EmailMessage;
import com.sgcor.shopply.message.EmailService;
import com.sgcor.shopply.shared.SharedService;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.Role;
import com.sgcor.shopply.user.User;
import com.sgcor.shopply.user.UserRepository;
import com.sgcor.shopply.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    public final UserService userService;
    public final JwtService jwtService;
    public final EmailService emailService;
    public final SharedService sharedService;

    public String registerUser(AuthDTO authDTO ) throws BadRequestException {
        User newUser = new User();
        String confirmationToken = UUID.randomUUID().toString();

        Optional<User> user = userRepository.findByUsername(authDTO.getUsername());
        User emailExist = userRepository.findByEmail(authDTO.getEmail());

        if (authDTO.getUsername() == null || authDTO.getPassword() == null || authDTO.getEmail() == null) {
            throw new BadRequestException("username, email and password is required");
        }

        if (user.isPresent()) {
            throw new BadRequestException("User with username already exist");
        }

        if (emailExist != null) {
            throw new BadRequestException("User with email already exist");
        }

        if (sharedService.isNotValidPassword(authDTO.getPassword())) {
            throw new BadRequestException("password must be minimum 6 characters, with an uppercase, lowercase and special character");
        }

        if (!userService.isValidEmail(authDTO.getEmail())) {
            throw new BadRequestException("Invalid email. Please provide a valid Email");
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
            throw new BadRequestException("username and password is required");
        }
        UserDetails userDetails = userService.loadUserByUsername(authDTO.getUsername());
        if (userDetails == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        if (passwordEncoder.matches(authDTO.getPassword(), userDetails.getPassword())) {
            return jwtService.generateToken(userDetails);
        } else {
            throw new UnauthorizedException("Authentication failed");
        }
    }
}

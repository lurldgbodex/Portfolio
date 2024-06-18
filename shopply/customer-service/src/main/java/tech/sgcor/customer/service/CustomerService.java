package tech.sgcor.customer.service;

import com.github.dockerjava.api.exception.BadRequestException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.customer.dto.*;
import tech.sgcor.customer.exception.InvalidTokenException;
import tech.sgcor.customer.exception.UserExistsException;
import tech.sgcor.customer.exception.UserNotFoundException;
import tech.sgcor.customer.model.User;
import tech.sgcor.customer.repository.UserRepository;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("customer with email not found"));
    }

    public CreateUserResponse registerUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserExistsException("customer already exists for this email");
        }

        User newUser = new User();
        newUser.setFirstName(request.getFirst_name());
        newUser.setLastName(request.getLast_name());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);

        if (request.getOther_name() != null) {
            newUser.setOtherName(request.getOther_name());
        }

        // create a confirmation token
        String token = UUID.randomUUID().toString();
        newUser.setConfirmationToken(token);

        newUser = userRepository.save(newUser);

        String path = "/api/users/" + newUser.getId();
        URI location = UriComponentsBuilder.fromPath(path).build().toUri();

        // todo: call notification service to send registration notification to customer

        CustomResponse responseMessage = new CustomResponse(201, "customer created successfully");

        return new CreateUserResponse(location, responseMessage);
    }

    public UserDto getUserDetails(String email) {
        User user = findByEmail(email);

        return UserDto
                .builder()
                .id(user.getId())
                .first_name(user.getFirstName())
                .last_name(user.getLastName())
                .other_name(user.getOtherName())
                .build();
    }

    public CustomResponse updateUserDetails(UpdateUserDetails request) {
        User user = findByEmail(request.getEmail());

        boolean requestBlank = Stream.of(
                request.getFirst_name(),
                request.getLast_name(),
                request.getOther_name()
        ).allMatch(StringUtils::isBlank);

        if (requestBlank) {
            throw new BadRequestException("provide the field you want to update");
        }

        if (request.getFirst_name() != null) {
            user.setFirstName(request.getFirst_name());
        }
        if (request.getLast_name() != null) {
            user.setLastName(request.getLast_name());
        }
        if (request.getOther_name() != null) {
            user.setLastName(request.getOther_name());
        }

        userRepository.save(user);

        return new CustomResponse(200, "customer details updated successfully");
    }

    public CustomResponse changePassword(ChangePasswordRequest request) {
        User user = findByEmail(request.getEmail());

        String old_password = passwordEncoder.encode(request.getOld_password());

        if (!user.getPassword().equals(old_password)) {
            throw new BadRequestException("incorrect password supplied");
        }

        if (!request.getNew_password().equals(request.getConfirm_password())) {
            throw new BadRequestException("password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(user);

        return new CustomResponse(200, "password change successful");
    }

    public CustomResponse confirmAccount(String token) {
        User user = userRepository.findByConfirmationToken(token)
                .orElseThrow(()-> new InvalidTokenException("Invalid confirmation token"));

        user.setEnabled(true);
        user.setConfirmationToken(null);
        userRepository.save(user);

        return new CustomResponse(200, "Account successfully confirmed");
    }

    public CustomResponse requestPasswordReset(String email) {
        User user = findByEmail(email);
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        // todo: send a email to customer with token to reset password

        return new CustomResponse(200, "password reset request successful");
    }

    public CustomResponse resetPassword(String token, ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(()-> new InvalidTokenException("Invalid password reset token"));

        if (!request.getNew_password().equals(request.getConfirm_password())) {
            throw new BadRequestException("password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(user);

        return new CustomResponse(200, "password has been reset");
    }
}

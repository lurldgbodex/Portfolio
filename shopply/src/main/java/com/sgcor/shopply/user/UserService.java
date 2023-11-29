package com.sgcor.shopply.user;

import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.SharedService;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.auth.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", Pattern.CASE_INSENSITIVE
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SharedService sharedService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username)
                );
    }
    public void save(User newUser) {
        userRepository.save(newUser);
    }

    public String findCurrentUser() throws UnauthorizedException {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication.isAuthenticated()) {
            return authentication.getName();
       } else {
           throw new UnauthorizedException("no current user");
       }
    }

    public boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    public GenericResponse updateUser(UserDetailDTO request) throws BadRequestException, UnauthorizedException {
        try {
            User user = (User) loadUserByUsername(findCurrentUser());

            if (request.getEmail() != null) {
                if (!isValidEmail(request.getEmail())) {
                    throw new BadRequestException("Please provide a valid email");
                }
                if (userRepository.findByEmail(request.getEmail()) != null){
                    throw new BadRequestException("User with email already exists");
                }
                user.setEmail(request.getEmail());
            }

            if (request.getDateOfBirth() != null) {
                try{
                    String date = request.getDateOfBirth();
                    DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dob = LocalDate.parse(date, dobFormatter);
                    user.setDateOfBirth(dob);
                } catch (Exception e) {
                    throw new BadRequestException("Invalid date format: " + "yyyy-mm-dd");
                }
            }

            user.setFirstName(request.getFirstName() != null ? request.getFirstName() : user.getFirstName());
            user.setLastName(request.getLastName() != null ? request.getLastName() : user.getLastName());
            user.setAddress(request.getAddress() != null ? request.getAddress() : user.getAddress());
            user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : user.getPhoneNumber());
            user.setImage(request.getImage() != null ? request.getImage() : user.getImage());
            save(user);
            return new GenericResponse("User updated successfully");

        } catch (UsernameNotFoundException une) {
            throw new UnauthorizedException(une.getMessage());
        }
    }

    public UserDetailDTO getUserDetails() throws Exception {
        User user = (User) loadUserByUsername(findCurrentUser());
        return new UserDetailDTO.UserDetailDTOBuilder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(String.valueOf(user.getDateOfBirth()))
                .phoneNumber(user.getPhoneNumber())
                .image((user.getImage()))
                .build();
    }

    public GenericResponse confirmUserEmail(String confirmationToken) throws BadRequestException {
        User user = userRepository.findByConfirmationToken(confirmationToken);
        if (user == null)
            throw new BadRequestException("Invalid Confirmation Token");
        if (user.getConfirmationTokenExpiration().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Expired confirmation Token");

        user.setIsConfirmed(IsConfirmed.True);
        save(user);

        return new GenericResponse("User confirmed successfully");
    }

    public GenericResponse changePassword(PasswordChangeRequest request) throws BadRequestException {
        try {
            if (request.getNewPassword() == null || request.getCurrentPassword() == null || request.getConfirmPassword() == null) {
                throw new BadRequestException("You need to provide your current password and new password");
            }
            User user = (User) loadUserByUsername(findCurrentUser());
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
                throw new IllegalStateException("Wrong password");
            }
            if (sharedService.isNotValidPassword(request.getNewPassword())) {
                throw new IllegalStateException("password must be minimum 6 characters, with an uppercase, lowercase and special character");
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalStateException("Password do not match");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            save(user);
            return new GenericResponse("password updated successfully");
        } catch (UsernameNotFoundException | UnauthorizedException unf) {
            throw new IllegalStateException(unf.getMessage());
        }
    }
}

package com.sgcor.shopply.user;

import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username)
                );
    }
    public void save(User newUser) {
        userRepository.save(newUser);
    }

    public String findCurrentUser() throws Exception {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication.isAuthenticated()) {
            return authentication.getName();
       } else {
           throw new Exception("no current user");
       }
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public GenericResponse updateUser(UserDetailDTO request) {
        try {
            User user = (User) loadUserByUsername(findCurrentUser());
            System.out.println(user);

            if (request.getEmail() != null) {
                if (userRepository.findByEmail(request.getEmail()) != null){
                    throw new RuntimeException("User with email already exists");
                }
                if (isValidEmail(request.getEmail())) {
                    user.setEmail(request.getEmail());
                } else {
                    throw new RuntimeException("Please provide a valid email");
                }
            }

            if (request.getDateOfBirth() != null) {
                try{
                    String date = request.getDateOfBirth();
                    DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dob = LocalDate.parse(date, dobFormatter);
                    user.setDateOfBirth(dob);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid date format: " + "yyyy-mm-dd");
                }
            }

            user.setFirstName(request.getFirstName() != null ? request.getFirstName() : user.getFirstName());
            user.setLastName(request.getLastName() != null ? request.getLastName() : user.getLastName());
            user.setAddress(request.getAddress() != null ? request.getAddress() : user.getAddress());
            user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : user.getPhoneNumber());
            user.setImage(request.getImage() != null ? request.getImage() : user.getImage());
            save(user);
            return new GenericResponse("User updated successfully");

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
}

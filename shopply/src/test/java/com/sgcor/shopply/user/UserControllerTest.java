package com.sgcor.shopply.user;

import com.sgcor.shopply.config.JwtService;
import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.auth.PasswordChangeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters=false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService underTest;
    @MockBean
    private JwtService jwtService;

    @Test
    void shouldCreateMockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void updateUserSuccess() throws Exception {
        UserDetailDTO request = new UserDetailDTO();
        when(underTest.updateUser(request)).thenReturn(new GenericResponse("User updated successfully"));

        mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    void updateUserUnauthorizedException() throws Exception {
        UserDetailDTO request = new UserDetailDTO();
        when(underTest.updateUser(request)).thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void updateUserBadRequestException() throws Exception {
        UserDetailDTO request = new UserDetailDTO();
        when(underTest.updateUser(request)).thenThrow(new BadRequestException("Bad request"));

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Bad request"));
    }

    @Test
    void getProfileSuccess() throws Exception {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        userDetailDTO.setFirstName("something");

        when(underTest.getUserDetails()).thenReturn(userDetailDTO);

        mockMvc.perform(get("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("firstName").value(userDetailDTO.getFirstName()));
    }

    @Test
    void getProfileException() throws Exception {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        when(underTest.getUserDetails()).thenThrow(new Exception("no user"));

        mockMvc.perform(get("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("no user"));
    }

    @Test
    void confirmEmailSuccess() throws Exception {
        String request = "GeneratedConfirmationToken";
        GenericResponse response = new GenericResponse("User confirmed successfully");
        when(underTest.confirmUserEmail(request)).thenReturn(response);

        mockMvc.perform(get("/api/users/" + request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void confirmEmailBadRequestException() throws Exception {
        String request = "GeneratedConfirmationToken";
        GenericResponse response = new GenericResponse("Invalid or Expired token");
        when(underTest.confirmUserEmail(request)).thenThrow(new BadRequestException(response.message()));

        mockMvc.perform(get("/api/users/" + request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void updatePasswordSuccess() throws Exception {
        GenericResponse response = new GenericResponse("password updated successfully");
        PasswordChangeRequest request = new PasswordChangeRequest();
        when(underTest.changePassword(request)).thenReturn(response);

        mockMvc.perform(patch("/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void updatePasswordIllegalStateException() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        GenericResponse response = new GenericResponse("Bad request");
        when(underTest.changePassword(request)).thenThrow(new IllegalStateException(response.message()));

        mockMvc.perform(patch("/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }
}
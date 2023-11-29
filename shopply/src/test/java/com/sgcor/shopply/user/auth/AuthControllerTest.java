package com.sgcor.shopply.user.auth;

import com.sgcor.shopply.config.JwtService;
import com.sgcor.shopply.shared.GenericResponse;
import com.sgcor.shopply.shared.exceptions.BadRequestException;
import com.sgcor.shopply.shared.exceptions.UnauthorizedException;
import com.sgcor.shopply.user.UserDetailDTO;
import com.sgcor.shopply.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters=false)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService underTest;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;

    @Test
    void shouldCreateMockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void registerUserSuccess() throws Exception {
        AuthDTO request = new AuthDTO();
        String token = "generatedToken";
        AuthResponse response = new AuthResponse(token);
        when(underTest.registerUser(request)).thenReturn(token);

        mockMvc.perform(post("/api/users/auths/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(response.token()));
    }

    @Test
    void registerUserBadRequest() throws Exception {
        AuthDTO request = new AuthDTO();
        GenericResponse response = new GenericResponse("bad request");
        when(underTest.registerUser(request)).thenThrow(new BadRequestException(response.message()));

        mockMvc.perform(post("/api/users/auths/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void loginSuccess() throws Exception{
        AuthDTO request = new AuthDTO();
        String token = "generatedToken";
        AuthResponse response = new AuthResponse(token);
        when(underTest.loginUser(request)).thenReturn(token);

        mockMvc.perform(post("/api/users/auths/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(response.token()));
    }

    @Test
    void loginUserBadRequest() throws Exception {
        AuthDTO request = new AuthDTO();
        GenericResponse response = new GenericResponse("bad request");
        when(underTest.loginUser(request)).thenThrow(new BadRequestException(response.message()));

        mockMvc.perform(post("/api/users/auths/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void loginUserNotFound() throws Exception {
        AuthDTO request = new AuthDTO();
        GenericResponse response = new GenericResponse("user not found");
        when(underTest.loginUser(request)).thenThrow(new UsernameNotFoundException(response.message()));

        mockMvc.perform(post("/api/users/auths/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }

    @Test
    void loginUserUnauthorized() throws Exception {
        AuthDTO request = new AuthDTO();
        GenericResponse response = new GenericResponse("Unauthorized");
        when(underTest.loginUser(request)).thenThrow(new UnauthorizedException(response.message()));

        mockMvc.perform(post("/api/users/auths/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(response.message()));
    }
}
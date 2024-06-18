//package tech.sgcor.customer.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
//import tech.sgcor.sharedservice.dto.CustomResponse;
//import tech.sgcor.customer.dto.*;
//import tech.sgcor.customer.service.CustomerService;
//
//import java.net.URI;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CustomerController.class)
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//    private CreateCustomerRequest newUserRequest;
//    private CustomerDto userDto;
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private CustomerService userService;
//
//    @BeforeEach()
//    void setup() {
//        // create a userRequest
//        newUserRequest = new CreateCustomerRequest();
//        newUserRequest.setFirst_name("new");
//        newUserRequest.setLast_name("customer");
//        newUserRequest.setOther_name("request");
//        newUserRequest.setEmail("new@customer.email");
//
//        // create userDto
//        userDto = CustomerDto
//                .builder()
//                .email(newUserRequest.getEmail())
//                .id(5L)
//                .last_name(newUserRequest.getLast_name())
//                .first_name(newUserRequest.getFirst_name())
//                .other_name(newUserRequest.getOther_name())
//                .build();
//    }
//
//    @Test
//    void shouldCreateMockMvc() {
//        assertThat(mockMvc).isNotNull();
//    }
//
//    @Test
//    void shouldGetUserByEmail() throws Exception {
//        GetRequest request = new GetRequest();
//        request.setEmail(newUserRequest.getEmail());
//
//        when(userService.getUserDetails(request)).thenReturn(userDto);
//
//        mockMvc.perform(get("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(newUserRequest.getEmail()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.email").value(newUserRequest.getEmail()))
//                .andExpect(jsonPath("$.id").value(5))
//                .andExpect(jsonPath("$.first_name").value(newUserRequest.getFirst_name()))
//                .andExpect(jsonPath("$.last_name").value(newUserRequest.getLast_name()))
//                .andExpect(jsonPath("$.other_name").value(newUserRequest.getOther_name()));
//
//        verify(userService).getUserDetails(request);
//    }
//
//    @Test
//    void shouldGetUserById() throws Exception {
//        when(userService.getUserById(5L)).thenReturn(userDto);
//
//        mockMvc.perform(get("/api/users/5"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userDto.getId()))
//                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
//                .andExpect(jsonPath("$.first_name").value(userDto.getFirst_name()))
//                .andExpect(jsonPath("$.last_name").value(userDto.getLast_name()))
//                .andExpect(jsonPath("other_name").value(userDto.getOther_name()));
//
//        verify(userService).getUserById(5L);
//    }
//
//    @Test
//    void shouldCreateNewUser() throws Exception {
//        CustomResponse res = new CustomResponse(201, "customer created");
//        URI location = URI.create("/api/users/5");
//        CreateCustomerResponse createUserResponse= new CreateCustomerResponse(location, res);
//
//        when(userService.registerUser(newUserRequest)).thenReturn(createUserResponse);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String request = objectMapper.writeValueAsString(newUserRequest);
//
//        mockMvc.perform(post("/api/users/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(request))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.code").value(201))
//                .andExpect(jsonPath("$.message").value(res.message()));
//
//        verify(userService).registerUser(newUserRequest);
//    }
//
//    @Test
//    void shouldUpdateUserDetail() throws Exception {
//        UpdateCustomerDetails updateRequest = new UpdateCustomerDetails();
//        updateRequest.setEmail(newUserRequest.getEmail());
//        updateRequest.setFirst_name("update firstname");
//        updateRequest.setLast_name("update lastname");
//        updateRequest.setOther_name("update other-name");
//
//        CustomResponse res = new CustomResponse(200, "updated");
//
//        when(userService.updateUserDetails(updateRequest)).thenReturn(res);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String request = objectMapper.writeValueAsString(updateRequest);
//
//        mockMvc.perform(put("/api/users/update")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(request))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(res.code()))
//                .andExpect(jsonPath("$.message").value(res.message()));
//
//        verify(userService).updateUserDetails(updateRequest);
//    }
//
//    @Test
//    void confirmAccount() throws Exception {
//        CustomResponse res = new CustomResponse(200, "account confirmed");
//        when(userService.confirmAccount("confirm-token")).thenReturn(res);
//
//        mockMvc.perform(put("/api/users/confirm-account?token=confirm-token")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value(res.message()));
//
//        verify(userService).confirmAccount("confirm-token");
//    }
//}
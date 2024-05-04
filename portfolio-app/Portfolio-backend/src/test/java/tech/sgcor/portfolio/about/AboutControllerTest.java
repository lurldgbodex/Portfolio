package tech.sgcor.portfolio.about;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tech.sgcor.portfolio.about.dto.CreateRequest;
import tech.sgcor.portfolio.about.dto.UpdateRequest;
import tech.sgcor.portfolio.about.entity.About;
import tech.sgcor.portfolio.about.repository.AboutRepository;
import tech.sgcor.portfolio.user.entity.User;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AboutControllerTest {
    private User user;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private AboutRepository aboutRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        user = new User();
        user.setFirstName("Jessica");
        user.setLastName("Dope");
        user.setImageUrl("https://image-url.com");

        About about = About
                .builder()
                .title("slay-queen")
                .dob(LocalDate.now())
                .email("jess@babe.com")
                .phoneNumber("+23418398329")
                .linkedin("@jess-babe")
                .github("@jess")
                .address("Abuja")
                .summary("fine babe. slaying master")
                .cvUrl("cvUrl")
                .user(user)
                .build();

        aboutRepository.saveAndFlush(about);
    }

    @Test
    void createMockMvcTest() {
        assertThat(mvc).isNotNull();
    }

    @Test
    @WithMockUser(username="user")
    void addAboutForbiddenTest() throws Exception {
        var req = new CreateRequest();
        mvc.perform(post("/api/admins/abouts/add")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addAboutSuccessTest() throws Exception {

        CreateRequest request = new CreateRequest();
        request.setFirst_name("John");
        request.setLast_name("Doe");
        request.setMiddle_name("Michael");
        request.setAddress("123 Main St");
        request.setDob("1990-01-01");
        request.setTitle("Software Engineer");
        request.setPhone_number("+123456789044");
        request.setSummary("Highly motivated developer");
        request.setEmail("john.doe@example.com");
        request.setGithub("https://github.com/johndoe");
        request.setLinkedin("https://www.linkedin.com/in/johndoe");
        request.setImage_url("https://example.com/profile.jpg");
        request.setCv("cv-link.com");

        // Build the multipart request with form data
        mvc.perform(post("/api/admins/abouts/add")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("address").value(request.getAddress()))
                .andExpect(jsonPath("title").value(request.getTitle()))
                .andExpect(jsonPath("email").value(request.getEmail()))
                .andExpect(jsonPath("dob").value(request.getDob()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateAboutSuccessTest() throws Exception {
        Long userId = user.getId();
        var req = new UpdateRequest();
        req.setMiddle_name("marve");

        String url = "/api/admins/abouts/" + userId;

        mvc.perform(patch(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code").value(200));
    }


    @Test
    @WithMockUser(username = "user")
    void updateAboutForbidden() throws Exception {
        long id = user.getId();
        var req = new UpdateRequest();

        String url = "/api/admins/abouts" + id;
        mvc.perform(patch(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteAboutSuccessTest() throws Exception {
        long userId = user.getId();

        String url = "/api/admins/abouts/" + userId;
        mvc.perform(delete(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code").value(200));
    }

    @Test
    void deleteAboutUnauthorized() throws Exception {
        long userId = user.getId();

        String url = "/api/admins/abouts/" + userId;
        mvc.perform(delete(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
package tech.sgcor.portfolio.about;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tech.sgcor.portfolio.user.User;
import tech.sgcor.portfolio.user.UserRepository;


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
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
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
                .linkedin("@jessbabe")
                .github("@jess")
                .address("Abuja")
                .summary("fine babe. slaying master")
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
        var req = new CreateRequest();
        req.setFirst_name("Jessica");
        req.setSummary("fine-girl");
        req.setGithub("https://github.com/jess");
        req.setAddress("LifeCamp, Abuja");
        req.setPhone_number("+23482954319");
        req.setLinkedin("https://linkedin.com/in/jess");
        req.setTitle("babe");
        req.setEmail("jess@babe.com");
        req.setDob("2002-11-21");
        req.setLast_name("dodo");
        req.setImage_url("image-url-link");

        mvc.perform(post("/api/admins/abouts/add")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("address").value(req.getAddress()))
                .andExpect(jsonPath("title").value(req.getTitle()))
                .andExpect(jsonPath("email").value(req.getEmail()))
                .andExpect(jsonPath("dob").value(req.getDob()));
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
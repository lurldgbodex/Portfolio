//package tech.sgcor.portfolio.about;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import tech.sgcor.portfolio.exceptions.ResourceNotFound;
//import tech.sgcor.portfolio.shared.CustomResponse;
//
//import java.util.HashMap;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@WebMvcTest(AboutController.class)
//class AboutControllerTest {
//    @Autowired
//    private MockMvc mvc;
//    @MockBean
//    private AboutService underTest;
//
//    @Test
//    void createMockMvcTest() {
//        assertThat(mvc).isNotNull();
//    }
//
////    @Test
////    void getAboutSuccessTest() throws Exception {
////        AboutResponse res = new AboutResponse(
////                10L, "test", "tit", "add", "mail",
////                "2000-04-04", "911", "sum", new HashMap<>()
////        );
////
////        given(underTest.getAbout(10L)).willReturn(res);
////
////        mvc.perform(get("/api/abouts/10")
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(jsonPath("name").value(res.name()));
////    }
//
//    @Test
//    void getAboutNotFoundTest() throws Exception {
//        given(underTest.getAbout(20L)).willThrow(new ResourceNotFound("not found"));
//
//        mvc.perform(get("/api/abouts/20")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("error").value("not found")
//        );
//    }
//
//
//    @Test
//    void addAboutSuccessTest() throws Exception {
//        var req = new CreateRequest();
//        req.setName("create about test");
//
//
//        var res = new About();
//
//        given(underTest.add(req)).willReturn(res);
//
//        mvc.perform(post("/api/abouts/add")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(req)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    void updateAboutSuccessTest() throws Exception {
//        Long id = 4L;
//
//        var req = new UpdateRequest();
//        req.setName("update about test");
//
//        var res = new CustomResponse(HttpStatus.OK.value(), "Updated successfully", HttpStatus.OK);
//
//        given(underTest.update(id, req)).willReturn(res);
//
//        mvc.perform(patch("/api/abouts/4")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("message").value(res.message()));
//    }
//
//
//    @Test
//    void updateAboutNotFoundTest() throws Exception {
//        long id = 20;
//        var req = new UpdateRequest();
//
//        when(underTest.update(id, req)).thenThrow(new ResourceNotFound("not found"));
//
//        mvc.perform(patch("/api/abouts/20")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(req))
//                )
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("error").value("not found")
//                );
//    }
//
//    @Test
//    void deleteAboutSuccessTest() throws Exception {
//
//        var res = new CustomResponse(HttpStatus.OK.value(), "deleted", HttpStatus.OK);
//
//        long id = 38;
//
//        given(underTest.delete(id)).willReturn(res);
//
//        mvc.perform(delete("/api/abouts/38")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("message").value(res.message()));
//    }
//
//    @Test
//    void deleteAboutNotFoundTest() throws Exception {
//        long id = 20;
//        given(underTest.delete(id)).willThrow(new ResourceNotFound("not found"));
//
//        mvc.perform(delete("/api/abouts/20")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("error").value("not found")
//                );
//    }
//}
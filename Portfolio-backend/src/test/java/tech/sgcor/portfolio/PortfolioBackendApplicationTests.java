package tech.sgcor.portfolio;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import tech.sgcor.portfolio.about.AboutResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/schema.sql", "/data.sql"})
class PortfolioBackendApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnAboutWhenDataIsSaved() {
		ResponseEntity<AboutResponse> response =
				restTemplate.getForEntity("/api/abouts/10", AboutResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		AboutResponse res = response.getBody();

		assertThat(res).isNotNull();
		assertThat(res.id()).isEqualTo(10);
		assertThat(res.name()).isEqualTo("test");
		assertThat(res.title()).isEqualTo("test-title");
		assertThat(res.address()).isEqualTo("test-address");
		assertThat(res.email()).isEqualTo("test-email");
		assertThat(res.phoneNumber()).isEqualTo("001122334455");
		assertThat(res.dob()).isEqualTo("2000-10-10");
		assertThat(res.summary()).isEqualTo("test-summary");
		assertThat(res.socials()).isNotNull();
		assertThat(res.socials()).hasFieldOrPropertyWithValue("github", "test-github-url");
		assertThat(res.socials()).hasFieldOrPropertyWithValue("linkedin", "test-linkedin-profile");
	}

	@Test
	void shouldNotReturnAboutWithUnknownId() {
		ResponseEntity<?> response =
				restTemplate.getForEntity("/api/abouts/39758982", Map.class);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		String errorMessage = documentContext.read("$.error");
		int errorCode = documentContext.read("$.code");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(documentContext).isNotNull();
		assertThat(errorMessage).isEqualTo("Data not found with id 39758982");
		assertThat(errorCode).isEqualTo(404);
	}

	@Test
	void contextLoads() {
	}

}

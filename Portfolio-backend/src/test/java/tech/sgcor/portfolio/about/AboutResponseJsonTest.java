package tech.sgcor.portfolio.about;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class AboutResponseJsonTest {

    @Autowired
    private JacksonTester<AboutResponse> json;

    @Test
    void aboutResponseSerializationTest() throws IOException {
        String dob = "2023-12-12";
        Map<String, String> socials = new HashMap<>();

        socials.put("github", "github-url");
        socials.put("linkedin", "linkedin-profile");

        AboutResponse response = new AboutResponse(
                10L,
                "Segun Clement",
                "Software Engineer",
                "Von Garden city",
                "gbodisegun@gmail.com",
                dob,
                "+2349153461938",
                "A proficient software engineer",
                socials
        );

        assertThat(json.write(response)).isStrictlyEqualToJson("expected.json");
        assertThat(json.write(response)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(response)).extractingJsonPathNumberValue("@.id").isEqualTo(10);
        assertThat(json.write(response)).hasJsonPathStringValue("@.name");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.name").isEqualTo("Segun Clement");
        assertThat(json.write(response)).hasJsonPathStringValue("@.title");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.title").isEqualTo("Software Engineer");
        assertThat(json.write(response)).hasJsonPathStringValue("@.address");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.address").isEqualTo("Von Garden city");
        assertThat(json.write(response)).hasJsonPathStringValue("@.email");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.email").isEqualTo("gbodisegun@gmail.com");
        assertThat(json.write(response)).hasJsonPathStringValue("@.dob");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.dob").isEqualTo(dob);
        assertThat(json.write(response)).hasJsonPathStringValue("@.phoneNumber");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.phoneNumber").isEqualTo("+2349153461938");
        assertThat(json.write(response)).hasJsonPathStringValue("@.summary");
        assertThat(json.write(response)).extractingJsonPathStringValue("@.summary").isEqualTo("A proficient software engineer");
        assertThat(json.write(response)).hasJsonPathMapValue("@.socials");
        assertThat(json.write(response)).extractingJsonPathMapValue("@.socials").hasFieldOrPropertyWithValue("github", "github-url");
        assertThat(json.write(response)).extractingJsonPathMapValue("@.socials").hasFieldOrPropertyWithValue("linkedin", "linkedin-profile");
    }

    @Test
    void aboutResponseDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 100,
                    "name": "Segun Clement",
                    "title": "Software Engineer",
                    "address": "Von Garden city",
                    "email": "gbodisegun@gmail.com",
                    "dob": "2023-12-12",
                    "phoneNumber": "+234007008009",
                    "summary": "A proficient software engineer",
                    "socials": {
                       "github": "github-url",
                       "linkedin": "linkedin-profile"
                    }
                }
                """;

        Map<String, String> socials = new HashMap<>();

        socials.put("github", "github-url");
        socials.put("linkedin", "linkedin-profile");
        AboutResponse jsonResponse = new AboutResponse(
                100L,
                "Segun Clement",
                "Software Engineer",
                "Von Garden city",
                "gbodisegun@gmail.com",
                "2023-12-12",
                "+234007008009",
                "A proficient software engineer",
                socials
        );


        assertThat(json.parse(expected)).isEqualTo(jsonResponse);
        assertThat(json.parseObject(expected).id()).isEqualTo(100);
        assertThat(json.parseObject(expected).name()).isEqualTo("Segun Clement");
        assertThat(json.parseObject(expected).title()).isEqualTo("Software Engineer");
        assertThat(json.parseObject(expected).address()).isEqualTo("Von Garden city");
        assertThat(json.parseObject(expected).email()).isEqualTo("gbodisegun@gmail.com");
        assertThat(json.parseObject(expected).dob()).isEqualTo("2023-12-12");
        assertThat(json.parseObject(expected).phoneNumber()).isEqualTo("+234007008009");
        assertThat(json.parseObject(expected).summary()).isEqualTo("A proficient software engineer");
        assertThat(json.parseObject(expected).socials().get("github")).isEqualTo("github-url");
        assertThat(json.parseObject(expected).socials().get("linkedin")).isEqualTo("linkedin-profile");
    }
}

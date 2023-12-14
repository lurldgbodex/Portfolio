package tech.sgcor.portfolio.shared;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CustomResponseJsonTest {
    @Autowired
    private JacksonTester<CustomResponse> json;

    @Test
    void customResponseSerializationTest() throws IOException {
        var underTest = new CustomResponse(201, "created successfully", HttpStatus.CREATED);

        assertThat(json.write(underTest)).isStrictlyEqualToJson("expectedResponse.json");
        assertThat(json.write(underTest)).hasJsonPathNumberValue("@.code");
        assertThat(json.write(underTest)).extractingJsonPathNumberValue("@.code").isEqualTo(201);
        assertThat(json.write(underTest)).hasJsonPathStringValue("message");
        assertThat(json.write(underTest)).extractingJsonPathStringValue("@.message").isEqualTo("created successfully");
        assertThat(json.write(underTest)).hasJsonPathValue("@.status");
        assertThat(json.write(underTest)).extractingJsonPathValue("@.status").isEqualTo("CREATED");
    }

    @Test
    void customErrorDeserializationTest() throws IOException {
        String expected = """
               {
                    "code": 200,
                    "message": "success",
                    "status": "OK"
               }
                """;

        var underTest = new CustomResponse(
                200, "success", HttpStatus.OK);

        assertThat(json.parse(expected)).isEqualTo(underTest);
        assertThat(json.parseObject(expected).code()).isEqualTo(200);
        assertThat(json.parseObject(expected).message()).isEqualTo("success");
        assertThat(json.parseObject(expected).status()).isEqualTo(HttpStatus.OK);
    }
}
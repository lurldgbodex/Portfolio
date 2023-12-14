package tech.sgcor.portfolio.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class CustomErrorTest {

    @Autowired
    private JacksonTester<CustomError> json;

    @Test
    void customErrorSerializationTest() throws IOException {
        CustomError underTest = new CustomError(400, "Bad Request", HttpStatus.BAD_REQUEST);

        assertThat(json.write(underTest)).isStrictlyEqualToJson("expectedError.json");
        assertThat(json.write(underTest)).hasJsonPathNumberValue("@.code");
        assertThat(json.write(underTest)).extractingJsonPathNumberValue("@.code").isEqualTo(400);
        assertThat(json.write(underTest)).hasJsonPathStringValue("error");
        assertThat(json.write(underTest)).extractingJsonPathStringValue("@.error").isEqualTo("Bad Request");
        assertThat(json.write(underTest)).hasJsonPathValue("@.status");
        assertThat(json.write(underTest)).extractingJsonPathValue("@.status").isEqualTo("BAD_REQUEST");
    }

    @Test
    void customErrorDeserializationTest() throws IOException {
        String expected = """
               {
                    "code": 401,
                    "error": "Unauthorized to access resource",
                    "status": "UNAUTHORIZED"
               }
                """;

        CustomError underTest = new CustomError(
                401, "Unauthorized to access resource", HttpStatus.UNAUTHORIZED);

        assertThat(json.parse(expected)).isEqualTo(underTest);
        assertThat(json.parseObject(expected).code()).isEqualTo(401);
        assertThat(json.parseObject(expected).error()).isEqualTo("Unauthorized to access resource");
        assertThat(json.parseObject(expected).status()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
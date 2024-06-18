package tech.sgcor.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import tech.sgcor.sharedservice.dto.CustomResponse;
import tech.sgcor.customer.dto.CreateCustomerRequest;
import tech.sgcor.customer.dto.CustomerDto;
import tech.sgcor.customer.model.Customer;
import tech.sgcor.customer.repository.CustomerRepository;
import tech.sgcor.customer.service.CustomerService;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApplicationTest {

    @LocalServerPort
    private int port;
    private final String baseUrl = "http://localhost:" + port + "/api/customers";
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
        Customer customer = new Customer();

        customer.setEmail("test@customer.com");
        customer.setPassword("LetMeInh@h@h@");
        customer.setFirstName("test");
        customer.setLastName("customer");

        customerRepository.save(customer);
    }

    @Test
    void shouldCreateAnewUserTest() {
        String url = baseUrl + "/create";

        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("test-customer@email.com");
        request.setFirst_name("test1");
        request.setLast_name("user1");
        request.setPassword("Password6.6.6.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateCustomerRequest> userRequest = new HttpEntity<>(request, headers);


        var res = testRestTemplate.exchange(
                url, HttpMethod.POST, userRequest, String.class);

        var body = res.getBody();
        URI location = res.getHeaders().getLocation();

        assert body != null;
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(body.code()).isEqualTo(201);
//        assertThat(body.message()).isEqualTo("customer created successfully");

        assertThat(location).isNotNull();
        assertThat(location).hasFragment(baseUrl + "/");
    }

    @Test
    void shouldGetUserDetailsByEmail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("test@email.com", headers);

        ResponseEntity<CustomerDto> customer = testRestTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, CustomerDto.class);

        CustomerDto res = customer.getBody();

        assertThat(customer.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

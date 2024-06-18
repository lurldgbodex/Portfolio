package tech.sgcor.customer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.sgcor.sharedservice.dto.CustomResponse;
import tech.sgcor.customer.dto.CreateCustomerRequest;
import tech.sgcor.customer.dto.GetRequest;
import tech.sgcor.customer.dto.UpdateCustomerDetails;
import tech.sgcor.customer.exception.InvalidTokenException;
import tech.sgcor.customer.exception.CustomerExistsException;
import tech.sgcor.customer.model.Customer;
import tech.sgcor.customer.repository.CustomerRepository;
import tech.sgcor.sharedservice.exception.BadRequestException;
import tech.sgcor.sharedservice.exception.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private Customer customer;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerService underTest;

    @BeforeEach
    void setup() {

        String email = "test@email.com";
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail(email);
        customer.setFirstName("Test");
        customer.setLastName("Customer");
    }

    @Test
    void findByEmail() {
        /**
         * successfully find valid customer by email
         */
        String email = "test@email.com";
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        var res = underTest.findByEmail(email);

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo(email);
        assertThat(res.getFirstName()).isEqualTo("Test");
        assertThat(res.getLastName()).isEqualTo("Customer");

        /**
         * throw a error if customer is does not exist
         */
        assertThatThrownBy(() -> underTest.findByEmail("invalid@customer.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("customer with email not found");
    }

    @Test
    void registerCustomer() {
        /**
         * when customer with email already exist
         */
        String email = "test@email.com";
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail(email);

        assertThatThrownBy(() -> underTest.registerCustomer(request))
                .isInstanceOf(CustomerExistsException.class)
                .hasMessageContaining("customer already exists for this email");

        /**
         * successfully create a customer
         */
        request.setEmail("new@customer.com");
        request.setFirst_name("New");
        request.setLast_name("customer");
        request.setOther_name("others");

        var res = underTest.registerCustomer(request);

        ArgumentCaptor<Customer> userArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).saveAndFlush(userArgumentCaptor.capture());
        Customer capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getConfirmationToken()).isNotNull();

        assertThat(res).isNotNull();
        assertThat(res.body()).isEqualTo(new CustomResponse(201, "customer created successfully"));
    }

    @Test
    void getCustomerDetails() {
        // find customer by email
        when(customerRepository.findByEmail(this.customer.getEmail())).thenReturn(Optional.of(customer));

        GetRequest request = new GetRequest();
        request.setEmail("test@email.com");
        var res = underTest.getCustomerDetails(request.getEmail());

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo("test@email.com");
        assertThat(res.getFirst_name()).isEqualTo("Test");
        assertThat(res.getLast_name()).isEqualTo("Customer");
        assertThat(res.getId()).isEqualTo(1);
        verify(customerRepository, times(1)).findByEmail("test@email.com");

        // find customer by id
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Long customerId = 1L;
        var byId = underTest.getCustomerById(customerId);

        assertThat(byId).isNotNull();
        assertThat(byId.getEmail()).isEqualTo("test@email.com");
        assertThat(byId.getId()).isEqualTo(1);
        verify(customerRepository, times(1)).findById(1L);

        // find customer with invalid id
        assertThatThrownBy(() -> underTest.getCustomerById(10L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("customer not found with id: " + 10L);
    }

    @Test
    void updateUserDetails() {
        // update with empty request body
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        UpdateCustomerDetails request = new UpdateCustomerDetails();
        request.setEmail(customer.getEmail());

        assertThatThrownBy(() -> underTest.updateCustomerDetails(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("provide the field you want to update");

        // successfully update customer
        request.setOther_name("others");
        CustomResponse response = underTest.updateCustomerDetails(request);

        verify(customerRepository, times(2)).findByEmail(customer.getEmail());
        verify(customerRepository).saveAndFlush(any(Customer.class));

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("customer details updated successfully");

    }

    @Test
    void confirmAccountTest() {
        /**
         * test with invalid token value
         */
        String confirmationToken = "valid-confirm-token";
        customer.setConfirmationToken(confirmationToken);

        String token = "invalid token";

        assertThatThrownBy(() -> underTest.confirmAccount(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid confirmation token");

        /**
         * successfully confirm account
         */
        when(customerRepository.findByConfirmationToken(confirmationToken)).thenReturn(Optional.of(customer));
        var res = underTest.confirmAccount(confirmationToken);
        assertThat(res).isNotNull();
        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("Account successfully confirmed");

        assertThat(customer.isEnabled()).isTrue();
        assertThat(customer.getConfirmationToken()).isNullOrEmpty();
    }
}
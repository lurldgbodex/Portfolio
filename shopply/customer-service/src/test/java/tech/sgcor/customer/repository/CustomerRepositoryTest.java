//package tech.sgcor.customer.repository;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import tech.sgcor.customer.model.Customer;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ExtendWith(MockitoExtension.class)
//class UserRepositoryTest {
//    private Customer customer;
//    @Autowired
//    private CustomerRepository underTest;
//
//    @BeforeEach
//    void setup () {
//        customer = new Customer();
//        customer.setEmail("test@mail.com");
//        customer.setFirstName("first-name");
//        customer.setLastName("last-name");
//        customer.setId(5L);
//        customer.setConfirmationToken("confirm-token");
//        underTest.save(customer);
//    }
//
//    @AfterEach
//    void tearDown() {
//        underTest.delete(customer);
//    }
//
//    @Test
//    void shouldFindByEmail() {
//       var res = underTest.findByEmail(customer.getEmail());
//       assertThat(res).isPresent();
//       assertThat(res).contains(customer);
//
//       // should return null if customer email not found
//        res = underTest.findByEmail("invalid@mail.com");
//        assertThat(res).isEmpty();
//    }
//
//    @Test
//    void findByConfirmationToken() {
//        var res = underTest.findByConfirmationToken(customer.getConfirmationToken());
//        assertThat(res).isPresent();
//        assertThat(res).contains(customer);
//
//        // should return null if customer not found with confirmation token
//        res = underTest.findByConfirmationToken("invalid-token");
//        assertThat(res).isEmpty();
//    }
//}
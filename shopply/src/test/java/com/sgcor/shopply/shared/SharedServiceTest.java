package com.sgcor.shopply.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class SharedServiceTest {

    @InjectMocks
    private SharedService underTest;

    @Test
    void isNotValidPasswordIfLengthLessThan6() {
        String password = "pass";
        boolean isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isTrue();
    }

    @Test
    void isNotValidPasswordIfNoSpecialCharacter() {
        String password = "password";
        boolean isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isTrue();
    }

    @Test
    void isNotValidPasswordIfNoNumber() {
        String password = "P@ssword";
        boolean  isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isTrue();
    }

    @Test
    void isNotValidPasswordIfNoUppercase() {
        String password = "p@ssword2";
        boolean  isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isTrue();
    }

    @Test
    void isNotValidPasswordIfNoLowercase() {
        String password = "P@SSWORD";
        boolean  isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isTrue();
    }

    @Test
    void isNotValidPassword() {
        String password = "P@ssword2";
        boolean  isNotValid = underTest.isNotValidPassword(password);

        assertThat(isNotValid).isFalse();
    }
}
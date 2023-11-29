package com.sgcor.shopply.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class GenericResponseTest {

    @Test
    void testGenericResponse() {
        String text = "some message";
        GenericResponse response = new GenericResponse(text);
        assertThat(response).hasFieldOrProperty("message");
        assertThat(response.message()).isEqualTo(text);
    }
}
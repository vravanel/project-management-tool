package com.exercice.project_management_tool;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class HttpAssertions {
    private HttpAssertions() {
    }

    public static void assertStatus(ThrowingCallable action, HttpStatus expected) {
        assertThatThrownBy(action)
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException)e).getStatusCode())
                    .isEqualTo(expected);
    }
}

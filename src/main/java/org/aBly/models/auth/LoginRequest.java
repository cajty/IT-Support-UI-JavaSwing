package org.aBly.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @JsonProperty("email")
    private final String email;

    @JsonProperty("password")
    private final String password;
}
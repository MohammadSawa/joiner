package io.appswave.joiner.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequest {

    @ToString.Exclude
    @Email(message = "validation.email.invalid")
    @NotBlank(message = "validation.email.required")
    private String email;

    @ToString.Exclude
    @NotBlank(message = "validation.password.required")
    private String password;
}

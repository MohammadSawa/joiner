package io.appswave.joiner.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
public class SignupRequest {

    @NotBlank(message = "validation.firstname.required")
    private String firstName;

    @NotBlank(message = "validation.lastname.required")
    private String lastName;

    @Email(message = "validation.email.invalid")
    @NotBlank(message = "validation.email.required")
    private String email;

    @ToString.Exclude
    @Size(min = 12, message = "validation.password.minlength")
    private String password;
}


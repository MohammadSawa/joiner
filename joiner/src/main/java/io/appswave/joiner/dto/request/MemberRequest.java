package io.appswave.joiner.dto.request;

import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class MemberRequest {

    @NotBlank(message = "validation.firstname.required")
    private String firstName;

    @NotBlank(message = "validation.lastname.required")
    private String lastName;

    @Email(message = "validation.email.invalid")
    @NotBlank(message = "validation.email.required")
    private String email;

    private String mobileNumber;

    @NotNull(message = "validation.gender.required")
    private Gender gender;

    @NotNull(message = "validation.membership.required")
    private MembershipType membershipType;

    @NotNull(message = "validation.persona.required")
    private PersonaType persona;
}

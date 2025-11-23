package io.appswave.joiner.dto.request;

import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberUpdateRequest {

    /**
     * If provided, must not be blank
     * If null, field is not updated
     */
    @NotBlank(message = "validation.firstname.required")
    private String firstName;

    @NotBlank(message = "validation.lastname.required")
    private String lastName;

    @Email(message = "validation.email.invalid")
    @NotBlank(message = "validation.email.required")
    private String email;

    @NotBlank(message = "validation.mobile.required")
    private String mobileNumber;

    private Gender gender;

    private MembershipType membershipType;

    private PersonaType persona;
}
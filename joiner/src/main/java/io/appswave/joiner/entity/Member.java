package io.appswave.joiner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobileNumber;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private MembershipType membershipType;

    @Column(nullable = false)
    private PersonaType persona;

    private boolean deleted = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}

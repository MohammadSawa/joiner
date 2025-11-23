package io.appswave.joiner.spec;

import io.appswave.joiner.entity.Member;
import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecifications {

    public static Specification<Member> notDeleted() {
        return (root, query, builder) ->
                builder.isFalse(root.get("deleted"));
    }

    public static Specification<Member> firstNameContains(String firstName) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Member> lastNameContains(String lastName) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Member> emailContains(String email) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Member> genderEquals(Gender gender) {
        return (root, query, builder) ->
                builder.equal(root.get("gender"), gender);
    }

    public static Specification<Member> membershipEquals(MembershipType membershipType) {
        return (root, query, builder) ->
                builder.equal(root.get("membershipType"), membershipType);
    }

    public static Specification<Member> personaEquals(PersonaType persona) {
        return (root, query, builder) ->
                builder.equal(root.get("persona"), persona);
    }
}

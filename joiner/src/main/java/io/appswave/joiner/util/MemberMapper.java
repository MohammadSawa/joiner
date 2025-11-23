package io.appswave.joiner.util;


import io.appswave.joiner.dto.response.MemberResponse;
import io.appswave.joiner.entity.Member;

public class MemberMapper {

    public static MemberResponse toDto(Member m) {
        return new MemberResponse(
                m.getId(),
                m.getFirstName(),
                m.getLastName(),
                m.getEmail(),
                m.getMobileNumber(),
                m.getGender().name(),
                m.getMembershipType().name(),
                m.getPersona().name()
        );
    }
}


package io.appswave.joiner.service;

import io.appswave.joiner.dto.request.MemberRequest;
import io.appswave.joiner.dto.request.MemberUpdateRequest;
import io.appswave.joiner.dto.response.MemberResponse;
import io.appswave.joiner.entity.Member;
import io.appswave.joiner.entity.User;
import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import io.appswave.joiner.enums.UserRole;
import io.appswave.joiner.repository.MemberRepository;
import io.appswave.joiner.repository.UserRepository;
import io.appswave.joiner.util.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private void assertAdmin() {
        if (currentUser().getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("user.unauthorized");
        }
    }

    private void assertOwnerOrAdmin(Member member) {
        User user = currentUser();
        boolean isOwner = member.getUser() != null &&
                member.getUser().getId().equals(user.getId());

        if (user.getUserRole() != UserRole.ADMIN && !isOwner) {
            throw new AccessDeniedException("user.unauthorized");
        }
    }

    public MemberResponse getMyProfile() {
        User user = currentUser();

        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("member.notfound"));

        return MemberMapper.toDto(member);
    }

    private Member getEntity(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("member.notfound"));
    }

    public MemberResponse create(MemberRequest request) {
        User user = currentUser();

        if (user.getUserRole() == UserRole.USER &&
                memberRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("member.profile.exists");
        }

        Member member = new Member();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setMobileNumber(request.getMobileNumber());
        member.setGender(request.getGender());
        member.setPersona(request.getPersona());
        member.setMembershipType(request.getMembershipType());

        if (user.getUserRole() == UserRole.USER) {
            member.setUser(user);
        }

        Member saved = memberRepository.save(member);
        return MemberMapper.toDto(saved);
    }

    public MemberResponse get(UUID id) {
        User user = currentUser();

        if (user.getUserRole() == UserRole.USER) {
            throw new AccessDeniedException("user.unauthorized");
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("member.notfound"));

        return MemberMapper.toDto(member);
    }


    public Page<MemberResponse> list(int page, int size, String search) {
        assertAdmin(); // Only admins may list all members

        Pageable pageable = PageRequest.of(page, size);

        Page<Member> pageResult = (search != null && !search.isBlank())
                ? memberRepository.searchActiveMembers(search, pageable)
                : memberRepository.findByDeletedFalse(pageable);

        return pageResult.map(MemberMapper::toDto);
    }

    public MemberResponse update(UUID id, MemberUpdateRequest request) {
        Member member = getEntity(id);

        // Update only if provided (not null) and validated
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            member.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            member.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            member.setEmail(request.getEmail());
        }
        if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
            member.setMobileNumber(request.getMobileNumber());
        }
        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }
        if (request.getMembershipType() != null) {
            member.setMembershipType(request.getMembershipType());
        }
        if (request.getPersona() != null) {
            member.setPersona(request.getPersona());
        }

        Member updated = memberRepository.save(member);
        return MemberMapper.toDto(updated);
    }

    public void softDelete(UUID id) {
        Member member = getEntity(id);
        member.setDeleted(true);
        memberRepository.save(member);
    }

    public void hardDelete(UUID id) {
        Member member = getEntity(id);
        memberRepository.delete(member);
    }

    public Page<MemberResponse> filterMembers(
            int page,
            int size,
            String firstName,
            String lastName,
            String email,
            String gender,
            String membershipType,
            String persona
    ) {
        // Authorization is already checked by @PreAuthorize on controller

        Pageable pageable = PageRequest.of(page, size);

        Gender genderEnum = (gender != null && !gender.isBlank())
                ? Gender.valueOf(gender.toUpperCase())
                : null;

        MembershipType membershipEnum = (membershipType != null && !membershipType.isBlank())
                ? MembershipType.valueOf(membershipType.toUpperCase())
                : null;

        PersonaType personaEnum = (persona != null && !persona.isBlank())
                ? PersonaType.valueOf(persona.toUpperCase())
                : null;

        Page<Member> pageResult = memberRepository.filterMembers(
                firstName, lastName, email, genderEnum, membershipEnum, personaEnum, pageable
        );

        return pageResult.map(MemberMapper::toDto);
    }
}

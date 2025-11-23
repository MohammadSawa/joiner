package io.appswave.joiner.service;

import io.appswave.joiner.entity.Member;
import io.appswave.joiner.entity.User;
import io.appswave.joiner.enums.UserRole;
import io.appswave.joiner.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberSecurityService {

    private final MemberRepository memberRepository;

    public boolean isOwnerOrAdmin(UUID memberId) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (currentUser.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return false;
        }

        return member.getUser() != null &&
                member.getUser().getId().equals(currentUser.getId());
    }
}
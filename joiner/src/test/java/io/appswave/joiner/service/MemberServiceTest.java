package io.appswave.joiner.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MemberService memberService;

    private User testUser;
    private Member testMember;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("john@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUserRole(UserRole.USER);

        testMember = new Member();
        testMember.setId(UUID.randomUUID());
        testMember.setFirstName("John");
        testMember.setLastName("Doe");
        testMember.setEmail("john@example.com");
        testMember.setMobileNumber("0790001111");
        testMember.setGender(Gender.MALE);
        testMember.setMembershipType(MembershipType.INTERNAL);
        testMember.setPersona(PersonaType.INDIVIDUAL);
        testMember.setUser(testUser);
        testMember.setDeleted(false);

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetMyProfileSuccess() {
        when(memberRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testMember));

        MemberResponse response = memberService.getMyProfile();

        assertNotNull(response);
        assertEquals(testMember.getId(), response.getId());
        assertEquals("John", response.getFirstName());
        verify(memberRepository, times(1)).findByUserId(testUser.getId());
    }

    @Test
    void testGetMyProfileNotFound() {
        when(memberRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            memberService.getMyProfile();
        });
    }

    @Test
    void testCreateMemberSuccess() {
        when(memberRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.create(new io.appswave.joiner.dto.request.MemberRequest());

        assertNotNull(response);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testCreateMemberUserAlreadyHasProfile() {
        when(memberRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testMember));

        assertThrows(RuntimeException.class, () -> {
            memberService.create(new io.appswave.joiner.dto.request.MemberRequest());
        });
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testGetMemberByIdAsAdmin() {
        testUser.setUserRole(UserRole.ADMIN);
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));

        MemberResponse response = memberService.get(testMember.getId());

        assertNotNull(response);
        assertEquals(testMember.getId(), response.getId());
        verify(memberRepository, times(1)).findById(testMember.getId());
    }

    @Test
    void testGetMemberByIdAsUserFails() {
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        
        assertThrows(Exception.class, () -> {
            memberService.get(testMember.getId());
        });
    }

    @Test
    void testSoftDeleteMember() {
        testUser.setUserRole(UserRole.ADMIN);
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        memberService.softDelete(testMember.getId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testHardDeleteMember() {
        testUser.setUserRole(UserRole.ADMIN);
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));


        memberService.hardDelete(testMember.getId());
        verify(memberRepository, times(1)).delete(testMember);
    }

    @Test
    void testUpdateMemberAsOwner() {
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setFirstName("John Updated");

        MemberResponse response = memberService.update(testMember.getId(), updateRequest);

        assertNotNull(response);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testUpdateMemberAsOtherUserFails() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        testMember.setUser(otherUser);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));

        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setFirstName("John Updated");

        assertThrows(Exception.class, () -> {
            memberService.update(testMember.getId(), updateRequest);
        });
    }
}
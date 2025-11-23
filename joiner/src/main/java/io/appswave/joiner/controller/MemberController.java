package io.appswave.joiner.controller;

import io.appswave.joiner.dto.request.MemberRequest;
import io.appswave.joiner.dto.request.MemberUpdateRequest;
import io.appswave.joiner.dto.response.ApiResponse;
import io.appswave.joiner.dto.response.DeleteResponse;
import io.appswave.joiner.dto.response.MemberResponse;
import io.appswave.joiner.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String membershipType,
            @RequestParam(required = false) String persona,
            Locale locale
    ) {
        Page<MemberResponse> members = memberService.filterMembers(
                page, size, firstName, lastName, email, gender, membershipType, persona
        );
        String msg = messageSource.getMessage("members.retrieved", null, "Members retrieved successfully", locale);
        return ResponseEntity.ok(ApiResponse.success(msg, members));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(
            @Valid @RequestBody MemberRequest request,
            Locale locale) {
        MemberResponse  member = memberService.create(request);
        String msg = messageSource.getMessage("member.created", null, "Member created successfully", locale);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(msg, member));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyProfile(Locale locale) {
        MemberResponse  member = memberService.getMyProfile();
        String msg = messageSource.getMessage("profile.retrieved", null, "Profile retrieved successfully", locale);
        return ResponseEntity.ok(ApiResponse.success(msg, member));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponse>> get(
            @PathVariable UUID id,
            Locale locale) {
        MemberResponse  member = memberService.get(id);
        String msg = messageSource.getMessage("member.retrieved", null, "Member retrieved successfully", locale);
        return ResponseEntity.ok(ApiResponse.success(msg, member));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@memberSecurityService.isOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<MemberResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody MemberUpdateRequest request,
            Locale locale) {
        MemberResponse  member = memberService.update(id, request);
        String msg = messageSource.getMessage("member.updated", null, "Member updated successfully", locale);
        return ResponseEntity.ok(ApiResponse.success(msg, member));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard,
            Locale locale
    ) {
        String message;
        String deleteType;

        if (hard) {
            memberService.hardDelete(id);
            message = messageSource.getMessage("member.permanently.deleted", null, "Member permanently deleted", locale);
            deleteType = "HARD";
        } else {
            memberService.softDelete(id);
            message = messageSource.getMessage("member.deleted", null, "Member deleted successfully", locale);
            deleteType = "SOFT";
        }

        DeleteResponse deleteResponse = new DeleteResponse(message, deleteType, true);
        return ResponseEntity.ok(ApiResponse.success(message, deleteResponse));
    }
}
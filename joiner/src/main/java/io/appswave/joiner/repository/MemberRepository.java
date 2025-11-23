package io.appswave.joiner.repository;

import io.appswave.joiner.entity.Member;
import io.appswave.joiner.enums.Gender;
import io.appswave.joiner.enums.MembershipType;
import io.appswave.joiner.enums.PersonaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {

    Optional<Member> findByUserId(UUID userId);

    @Query("""
        SELECT m FROM Member m
        WHERE m.deleted = false
        AND (:firstName IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
        AND (:lastName IS NULL OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
        AND (:email IS NULL OR LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%')))
        AND (:gender IS NULL OR m.gender = :gender)
        AND (:membershipType IS NULL OR m.membershipType = :membershipType)
        AND (:persona IS NULL OR m.persona = :persona)
    """)
    Page<Member> filterMembers(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("gender") Gender gender,
            @Param("membershipType") MembershipType membershipType,
            @Param("persona") PersonaType persona,
            Pageable pageable
    );

    Page<Member> findByDeletedFalse(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.deleted = false AND " +
            "(LOWER(m.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(m.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Member> searchActiveMembers(@Param("search") String search, Pageable pageable);

}


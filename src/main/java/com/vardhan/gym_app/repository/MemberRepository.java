package com.vardhan.gym_app.repository;

import com.vardhan.gym_app.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Member> findAllByOrderByFullNameAsc();

    @Query("SELECT m FROM Member m " +
            "WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY m.fullName")
    List<Member> search(@Param("keyword") String keyword);

    long countByJoinDateBetween(LocalDate start, LocalDate end);
}
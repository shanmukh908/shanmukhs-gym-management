package com.vardhan.gym_app.repository;

import com.vardhan.gym_app.dto.PlanCount;
import com.vardhan.gym_app.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.member " +
            "JOIN FETCH e.plan " +
            "ORDER BY e.startDate DESC, e.id DESC")
    List<Enrollment> findAllWithDetails();

    boolean existsByMemberIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long memberId, LocalDate newEnd, LocalDate newStart);

    boolean existsByMemberId(Long memberId);

    boolean existsByPlanId(Long planId);

    @Query("SELECT COUNT(DISTINCT e.member.id) FROM Enrollment e " +
            "WHERE e.startDate <= :today AND e.endDate >= :today")
    long countActiveMembers(@Param("today") LocalDate today);

    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.member JOIN FETCH e.plan " +
            "WHERE e.endDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND NOT EXISTS (SELECT 1 FROM Enrollment n " +
            "                WHERE n.member.id = e.member.id AND n.endDate > e.endDate) " +
            "ORDER BY e.endDate ASC")
    List<Enrollment> findEndingBetweenWithoutRenewal(@Param("rangeStart") LocalDate rangeStart,
                                                     @Param("rangeEnd") LocalDate rangeEnd);

    @Query("SELECT SUM(e.pricePaid) FROM Enrollment e " +
            "WHERE e.enrolledOn BETWEEN :rangeStart AND :rangeEnd")
    BigDecimal sumRevenueBetween(@Param("rangeStart") LocalDate rangeStart,
                                 @Param("rangeEnd") LocalDate rangeEnd);

    @Query("SELECT new com.vardhan.gym_app.dto.PlanCount(e.plan.name, COUNT(e)) " +
            "FROM Enrollment e " +
            "WHERE e.startDate <= :today AND e.endDate >= :today " +
            "GROUP BY e.plan.name " +
            "ORDER BY COUNT(e) DESC")
    List<PlanCount> countActiveByPlan(@Param("today") LocalDate today);
}
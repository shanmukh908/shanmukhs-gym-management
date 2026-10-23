package com.vardhan.gym_app.service;

import com.vardhan.gym_app.dto.DashboardData;
import com.vardhan.gym_app.dto.PlanCount;
import com.vardhan.gym_app.entity.Enrollment;
import com.vardhan.gym_app.repository.EnrollmentRepository;
import com.vardhan.gym_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public DashboardData getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = YearMonth.from(today).atEndOfMonth();

        long totalMembers = memberRepository.count();
        long newThisMonth = memberRepository.countByJoinDateBetween(monthStart, monthEnd);
        long activeMembers = enrollmentRepository.countActiveMembers(today);

        List<Enrollment> expiringSoon =
                enrollmentRepository.findEndingBetweenWithoutRenewal(today, today.plusDays(7));
        List<Enrollment> recentlyExpired =
                enrollmentRepository.findEndingBetweenWithoutRenewal(today.minusDays(30), today.minusDays(1));

        BigDecimal revenue = enrollmentRepository.sumRevenueBetween(monthStart, monthEnd);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        List<PlanCount> breakdown = enrollmentRepository.countActiveByPlan(today);
        long planTotal = breakdown.stream().mapToLong(PlanCount::getCount).sum();

        return DashboardData.builder()
                .totalMembers(totalMembers)
                .newMembersThisMonth(newThisMonth)
                .activeMembers(activeMembers)
                .inactiveMembers(Math.max(0, totalMembers - activeMembers))
                .expiringSoonCount(expiringSoon.size())
                .revenueThisMonth(revenue)
                .monthLabel(today.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .expiringSoon(expiringSoon)
                .recentlyExpired(recentlyExpired)
                .planBreakdown(breakdown)
                .planTotal(planTotal)
                .build();
    }
}
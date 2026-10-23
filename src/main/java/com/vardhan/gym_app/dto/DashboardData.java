package com.vardhan.gym_app.dto;

import com.vardhan.gym_app.entity.Enrollment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardData {

    private long totalMembers;
    private long newMembersThisMonth;
    private long activeMembers;
    private long inactiveMembers;
    private int expiringSoonCount;
    private BigDecimal revenueThisMonth;
    private String monthLabel;

    private List<Enrollment> expiringSoon;
    private List<Enrollment> recentlyExpired;
    private List<PlanCount> planBreakdown;
    private long planTotal;
}
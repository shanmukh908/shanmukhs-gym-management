package com.vardhan.gym_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;

    private LocalDate enrolledOn;

    public EnrollmentStatus getStatus() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return EnrollmentStatus.UPCOMING;
        }
        if (today.isAfter(endDate)) {
            return EnrollmentStatus.EXPIRED;
        }
        return EnrollmentStatus.ACTIVE;
    }

    public long getDaysLeft() {
        if (getStatus() == EnrollmentStatus.EXPIRED) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public long getDaysSinceExpiry() {
        return ChronoUnit.DAYS.between(endDate, LocalDate.now());
    }
}
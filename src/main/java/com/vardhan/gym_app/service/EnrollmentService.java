package com.vardhan.gym_app.service;

import com.vardhan.gym_app.dto.EnrollmentForm;
import com.vardhan.gym_app.entity.Enrollment;
import com.vardhan.gym_app.entity.Member;
import com.vardhan.gym_app.entity.Plan;
import com.vardhan.gym_app.exception.BusinessRuleException;
import com.vardhan.gym_app.repository.EnrollmentRepository;
import com.vardhan.gym_app.repository.MemberRepository;
import com.vardhan.gym_app.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithDetails();
    }

    @Transactional
    public Enrollment enroll(EnrollmentForm form) {

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new BusinessRuleException("The selected member does not exist."));

        Plan plan = planRepository.findById(form.getPlanId())
                .orElseThrow(() -> new BusinessRuleException("The selected plan does not exist."));

        if (!plan.isActive()) {
            throw new BusinessRuleException("The " + plan.getName() + " plan is inactive and cannot be sold.");
        }

        LocalDate start = form.getStartDate();
        LocalDate end = start.plusMonths(plan.getDurationInMonths()).minusDays(1);

        boolean overlaps = enrollmentRepository
                .existsByMemberIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(member.getId(), end, start);

        if (overlaps) {
            throw new BusinessRuleException(member.getFullName()
                    + " already has a membership overlapping these dates. "
                    + "Choose a start date after the current membership ends.");
        }

        Enrollment enrollment = Enrollment.builder()
                .member(member)
                .plan(plan)
                .startDate(start)
                .endDate(end)
                .pricePaid(plan.getPrice())
                .enrolledOn(LocalDate.now())
                .build();

        return enrollmentRepository.save(enrollment);
    }
}
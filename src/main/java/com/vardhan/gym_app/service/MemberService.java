package com.vardhan.gym_app.service;

import com.vardhan.gym_app.dto.MemberForm;
import com.vardhan.gym_app.entity.Member;
import com.vardhan.gym_app.exception.BusinessRuleException;
import com.vardhan.gym_app.repository.EnrollmentRepository;
import com.vardhan.gym_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Member> getMembers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return memberRepository.findAllByOrderByFullNameAsc();
        }
        return memberRepository.search(keyword.trim());
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    public boolean emailTaken(String email, Long currentMemberId) {
        String cleanEmail = email.trim();
        if (currentMemberId == null) {
            return memberRepository.existsByEmailIgnoreCase(cleanEmail);
        }
        return memberRepository.existsByEmailIgnoreCaseAndIdNot(cleanEmail, currentMemberId);
    }

    public void createMember(MemberForm form) {
        Member member = Member.builder()
                .fullName(form.getFullName().trim())
                .email(form.getEmail().trim().toLowerCase())
                .phone(form.getPhone())
                .gender(form.getGender())
                .dateOfBirth(form.getDateOfBirth())
                .joinDate(LocalDate.now())
                .build();
        memberRepository.save(member);
    }

    public MemberForm toForm(Member member) {
        MemberForm form = new MemberForm();
        form.setFullName(member.getFullName());
        form.setEmail(member.getEmail());
        form.setPhone(member.getPhone());
        form.setGender(member.getGender());
        form.setDateOfBirth(member.getDateOfBirth());
        return form;
    }

    public void updateMember(Long id, MemberForm form) {
        Member member = getMember(id);
        member.setFullName(form.getFullName().trim());
        member.setEmail(form.getEmail().trim().toLowerCase());
        member.setPhone(form.getPhone());
        member.setGender(form.getGender());
        member.setDateOfBirth(form.getDateOfBirth());
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        getMember(id);
        if (enrollmentRepository.existsByMemberId(id)) {
            throw new BusinessRuleException(
                    "This member has membership history and cannot be deleted.");
        }
        memberRepository.deleteById(id);
    }
}
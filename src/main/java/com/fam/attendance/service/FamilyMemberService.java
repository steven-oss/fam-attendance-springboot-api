package com.fam.attendance.service;

import com.fam.attendance.dto.CreateFamilyMemberRequest;
import com.fam.attendance.dto.FamilyMemberDto;
import com.fam.attendance.dto.PageResponse;
import com.fam.attendance.entity.FamilyMember;
import com.fam.attendance.repository.FamilyMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FamilyMemberService {

    private static final int MAX_PAGE_SIZE = 100;

    private final FamilyMemberRepository familyMemberRepository;

    public FamilyMemberService(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<FamilyMemberDto> findPage(int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.ASC, "id"));

        Page<FamilyMember> result = familyMemberRepository.findAll(pageable);
        return new PageResponse<>(
                result.getContent().stream().map(FamilyMemberDto::from).toList(),
                result.getTotalElements(),
                safePage,
                safeSize,
                result.getTotalPages());
    }

    @Transactional
    public FamilyMemberDto create(CreateFamilyMemberRequest request) {
        FamilyMember entity = new FamilyMember(
            request.name(),
            request.gender(),
            request.phone(),
            request.address()
        );
        FamilyMember savedEntity = familyMemberRepository.save(entity);
        return FamilyMemberDto.from(savedEntity);
    }
}

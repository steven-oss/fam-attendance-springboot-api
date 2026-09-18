package com.fam.attendance.dto;

import com.fam.attendance.entity.FamilyMember;

public record FamilyMemberDto(
        Long id,
        String name,
        String gender,
        String phone,
        String address) {

    public static FamilyMemberDto from(FamilyMember entity) {
        return new FamilyMemberDto(
                entity.getId(),
                entity.getName(),
                entity.getGender(),
                entity.getPhone(),
                entity.getAddress());
    }
}
package com.fam.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFamilyMemberRequest(@NotBlank String name,
     String gender,
     String phone,
     String address) {
}
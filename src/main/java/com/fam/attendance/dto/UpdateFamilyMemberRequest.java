package com.fam.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFamilyMemberRequest(@NotBlank String name,
     String gender,
     String phone,
     String address) {
}
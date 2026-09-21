package com.fam.attendance.controller;

import com.fam.attendance.dto.CreateFamilyMemberRequest;
import com.fam.attendance.dto.FamilyMemberDto;
import com.fam.attendance.dto.GenderOptionDto;
import com.fam.attendance.dto.PageResponse;
import com.fam.attendance.dto.UpdateFamilyMemberRequest;
import com.fam.attendance.entity.enums.Gender;
import com.fam.attendance.service.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "FamAttendance", description = "家人總名單 API")
@RestController
@RequestMapping("/fam-attendance")
public class FamAttendanceController {

    private final FamilyMemberService familyMemberService;

    public FamAttendanceController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    @Operation(summary = "家人總名單（分頁查詢）")
    @GetMapping("/page")
    public PageResponse<FamilyMemberDto> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return familyMemberService.findPage(page, pageSize);
    }

    @Operation(summary = "性別選項（下拉選單）")
    @GetMapping("/gender-options")
    public List<GenderOptionDto> genderOptions() {
        return Arrays.stream(Gender.values())
                .map(g -> new GenderOptionDto(g.getCode(), g.getLabel()))
                .toList();
    }

    @Operation(summary = "新增家人")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyMemberDto create(@RequestBody @Valid CreateFamilyMemberRequest request) {
        return familyMemberService.create(request);
    }

    @Operation(summary = "查詢家人詳細資料")
    @GetMapping("/{id}")
    public FamilyMemberDto findById(@PathVariable Long id) {
        return familyMemberService.findById(id);
    }

    @Operation(summary = "更新家人資料")
    @PutMapping("/{id}")
    public FamilyMemberDto update(@PathVariable Long id, @RequestBody @Valid UpdateFamilyMemberRequest request) {
        return familyMemberService.update(id, request);
    }

    @Operation(summary = "刪除家人")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        familyMemberService.delete(id);
    }
}

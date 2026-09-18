package com.fam.attendance.controller;

import com.fam.attendance.dto.FamilyMemberDto;
import com.fam.attendance.dto.PageResponse;
import com.fam.attendance.service.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.fam.attendance.dto.CreateFamilyMemberRequest;

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

    @Operation(summary = "新增家人")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyMemberDto create(@RequestBody @Valid CreateFamilyMemberRequest request){
        return familyMemberService.create(request);
    }
}

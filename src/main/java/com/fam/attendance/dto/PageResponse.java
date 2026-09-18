package com.fam.attendance.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> records,
        long total,
        int page,
        int pageSize,
        int totalPages) {
}

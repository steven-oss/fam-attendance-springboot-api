package com.fam.attendance.entity.enums;

public enum Gender {
    MALE("M", "男"),
    FEMALE("F", "女"),
    OTHER("O", "其他");

    private final String code;
    private final String label;

    Gender(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
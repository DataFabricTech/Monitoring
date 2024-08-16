package com.mobigen.monitoring.model.dto;

import lombok.Getter;

@Getter
public class SchedulerSettingDto {
    private String collectExpression;
    private String saveExpression;
}

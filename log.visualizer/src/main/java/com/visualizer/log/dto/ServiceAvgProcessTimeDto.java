package com.visualizer.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ServiceAvgProcessTimeDto {

    private String tagId;

    private Double avgProcessTime;

}

package com.visualizer.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ServiceMaxProcessTimeDiffDto {

    private String tagId;

    private Double maxProcessTimeDiff;

}

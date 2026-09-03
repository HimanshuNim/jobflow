package com.himanshu.jobflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationSummaryResponse {

    private long total;
    private long applied;
    private long interview;
    private long offer;
    private long rejected;
}

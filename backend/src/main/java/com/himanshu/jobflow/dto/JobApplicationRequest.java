package com.himanshu.jobflow.dto;

import com.himanshu.jobflow.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String jobTitle;

    @NotNull
    private ApplicationStatus status;

    private String jobUrl;

    private Long resumeId;
}

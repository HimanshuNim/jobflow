package com.himanshu.jobflow.dto;

import com.himanshu.jobflow.entity.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobApplicationResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private ApplicationStatus status;
    private String jobUrl;
    private LocalDateTime appliedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long resumeId;
    private String resumeName;
    private Integer resumeVersion;
    private String resumeFileName;
}
package com.himanshu.jobflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResumeResponse {

    private Long id;
    private String name;
    private Integer version;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
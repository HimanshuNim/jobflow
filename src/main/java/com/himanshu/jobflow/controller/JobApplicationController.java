package com.himanshu.jobflow.controller;


import com.himanshu.jobflow.dto.ApplicationSummaryResponse;
import com.himanshu.jobflow.dto.JobApplicationRequest;
import com.himanshu.jobflow.dto.JobApplicationResponse;
import com.himanshu.jobflow.dto.JobApplicationUpdateRequest;
import com.himanshu.jobflow.entity.ApplicationStatus;
import com.himanshu.jobflow.exception.InvalidStatusException;
import com.himanshu.jobflow.service.JobApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@SecurityRequirement(name = "bearerAuth")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createdApplication(
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication) {

        jobApplicationService.createApplication(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public Page<JobApplicationResponse> getApplications(
            @RequestParam(required = false) String status,
            @ParameterObject
            @PageableDefault(size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            Authentication authentication) {

        ApplicationStatus applicationStatus = null;

        if (status != null) {
            try {
                applicationStatus = ApplicationStatus.valueOf(
                        status.toUpperCase()
                );
            } catch (IllegalArgumentException exception) {
                throw new InvalidStatusException(
                        "Invalid status. Allowed values: APPLIED, INTERVIEW, OFFER, REJECTED"
                );
            }
        }

        return jobApplicationService.getApplications(
                authentication.getName(),
                applicationStatus,
                pageable
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationUpdateRequest request,
            Authentication authentication) {

        jobApplicationService.updateApplication(
                id,
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        jobApplicationService.deleteApplication(
                id,
                authentication.getName()
        );
    }

    @GetMapping("/summary")
    public ApplicationSummaryResponse getApplicationSummary(
            Authentication authentication) {

        return jobApplicationService.getApplicationSummary(
                authentication.getName()
        );
    }
}

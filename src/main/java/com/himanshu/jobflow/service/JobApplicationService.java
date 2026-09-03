package com.himanshu.jobflow.service;

import com.himanshu.jobflow.dto.JobApplicationRequest;
import com.himanshu.jobflow.dto.JobApplicationResponse;
import com.himanshu.jobflow.dto.JobApplicationUpdateRequest;
import com.himanshu.jobflow.entity.ApplicationStatus;
import com.himanshu.jobflow.entity.JobApplication;
import com.himanshu.jobflow.entity.User;
import com.himanshu.jobflow.exception.ApplicationNotFoundException;
import com.himanshu.jobflow.exception.UnauthorizedApplicationException;
import com.himanshu.jobflow.repository.JobApplicationRepository;
import com.himanshu.jobflow.repository.UserRepository;
import com.himanshu.jobflow.dto.ApplicationSummaryResponse;
import com.himanshu.jobflow.entity.Resume;
import com.himanshu.jobflow.repository.ResumeRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository, ResumeRepository resumeRepository) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    public void createApplication(
            JobApplicationRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = new JobApplication();

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setStatus(request.getStatus());
        application.setJobUrl(request.getJobUrl());
        application.setUser(user);

        if (request.getResumeId() != null) {

            Resume resume = resumeRepository
                    .findByIdAndUser(request.getResumeId(), user)
                    .orElseThrow(() ->
                            new RuntimeException("Resume not found")
                    );

            application.setResume(resume);
        }

        jobApplicationRepository.save(application);
    }

    public Page<JobApplicationResponse> getApplications(
            String email,
            ApplicationStatus status,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<JobApplication> applications;

        if (status == null) {
            applications = jobApplicationRepository.findByUserId(
                    user.getId(),
                    pageable
            );
        } else {
            applications = jobApplicationRepository.findByUserIdAndStatus(
                    user.getId(),
                    status,
                    pageable
            );
        }

        return applications.map(application -> {

            JobApplicationResponse response = new JobApplicationResponse();

            response.setId(application.getId());
            response.setCompanyName(application.getCompanyName());
            response.setJobTitle(application.getJobTitle());
            response.setStatus(application.getStatus());
            response.setJobUrl(application.getJobUrl());
            response.setAppliedAt(application.getAppliedAt());
            response.setCreatedAt(application.getCreatedAt());
            response.setUpdatedAt(application.getUpdatedAt());

            if (application.getResume() != null) {
                response.setResumeId(application.getResume().getId());
                response.setResumeName(application.getResume().getName());
                response.setResumeVersion(application.getResume().getVersion());
                response.setResumeFileName(application.getResume().getFileName());
            }

            return response;
        });
    }

    public void updateApplication(
            Long applicationId,
            JobApplicationUpdateRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedApplicationException("You are not allowed to update this application");
        }

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setStatus(request.getStatus());
        application.setJobUrl(request.getJobUrl());

        if (request.getResumeId() != null) {

            Resume resume = resumeRepository
                    .findByIdAndUser(request.getResumeId(), user)
                    .orElseThrow(() ->
                            new RuntimeException("Resume not found")
                    );

            application.setResume(resume);

        } else {
            application.setResume(null);
        }

        jobApplicationRepository.save(application);
    }

    public void deleteApplication(Long applicationId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedApplicationException("You are not allowed to delete this application");
        }

        jobApplicationRepository.delete(application);
    }

    public ApplicationSummaryResponse getApplicationSummary(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        ApplicationSummaryResponse response = new ApplicationSummaryResponse();

        response.setTotal(
                jobApplicationRepository.countByUserId(userId)
        );

        response.setApplied(
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.APPLIED
                )
        );

        response.setInterview(
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.INTERVIEW
                )
        );

        response.setOffer(
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.OFFER
                )
        );

        response.setRejected(
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.REJECTED
                )
        );

        return response;
    }
}
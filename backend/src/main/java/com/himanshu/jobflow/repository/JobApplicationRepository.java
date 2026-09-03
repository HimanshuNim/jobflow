package com.himanshu.jobflow.repository;

import com.himanshu.jobflow.entity.ApplicationStatus;
import com.himanshu.jobflow.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    Page<JobApplication> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<JobApplication> findByUserIdAndStatus(
            Long userId,
            ApplicationStatus status
    );

    Page<JobApplication> findByUserIdAndStatus(
            Long userId,
            ApplicationStatus status,
            Pageable pageable
    );

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, ApplicationStatus status);
}
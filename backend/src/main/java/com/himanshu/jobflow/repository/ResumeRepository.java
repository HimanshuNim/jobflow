package com.himanshu.jobflow.repository;

import com.himanshu.jobflow.entity.Resume;
import com.himanshu.jobflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);

    Optional<Resume> findByIdAndUser(Long id, User user);

    int countByUser(User user);
}

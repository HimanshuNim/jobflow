package com.himanshu.jobflow.service;

import com.himanshu.jobflow.entity.Resume;
import com.himanshu.jobflow.entity.User;
import com.himanshu.jobflow.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import com.himanshu.jobflow.exception.ResumeNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> getUserResumes(User user) {
        return resumeRepository.findByUser(user);
    }

    public Resume getUserResume(Long id, User user) {
        return resumeRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResumeNotFoundException("Resume not found")
                );
    }

    public int getNextVersion(User user) {
        return resumeRepository.countByUser(user) + 1;
    }

    public Resume saveResume(Resume resume) {
        return resumeRepository.save(resume);
    }

    public void deleteResume(Long id, User user) {

        Resume resume = getUserResume(id, user);

        resumeRepository.delete(resume);
    }
}
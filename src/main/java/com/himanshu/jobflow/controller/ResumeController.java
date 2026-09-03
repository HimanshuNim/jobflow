package com.himanshu.jobflow.controller;

import com.himanshu.jobflow.service.BackblazeStorageService;

import com.himanshu.jobflow.dto.ResumeResponse;
import com.himanshu.jobflow.entity.Resume;
import com.himanshu.jobflow.entity.User;
import com.himanshu.jobflow.service.ResumeService;
import com.himanshu.jobflow.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final BackblazeStorageService storageService;

    public ResumeController(
            ResumeService resumeService,
            UserService userService, BackblazeStorageService storageService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping
    public List<ResumeResponse> getResumes(
            Authentication authentication) {

        User user = userService.getUserEntityByEmail(
                authentication.getName()
        );

        return resumeService.getUserResumes(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResumeResponse getResume(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getUserEntityByEmail(
                authentication.getName()
        );

        Resume resume = resumeService.getUserResume(id, user);

        return toResponse(resume);
    }

    @DeleteMapping("/{id}")
    public String deleteResume(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getUserEntityByEmail(
                authentication.getName()
        );

        Resume resume = resumeService.getUserResume(id, user);

        // Delete file from Backblaze B2 if it is stored there
        if (resume.getStorageKey() != null
                && !resume.getStorageKey().isBlank()) {

            storageService.deleteFile(resume.getStorageKey());
        }

        // Delete resume record from database
        resumeService.deleteResume(id, user);

        return "Resume deleted successfully";
    }


    @Operation(summary = "Upload a resume")
    @PostMapping(consumes = "multipart/form-data")
    public ResumeResponse uploadResume(
            @RequestParam("name") String name,
            @Parameter(description = "Resume file")
            @RequestPart("file") MultipartFile file,
            Authentication authentication) throws IOException {

        User user = userService.getUserEntityByEmail(authentication.getName());

        if (file.isEmpty()) {
            throw new RuntimeException("Resume file cannot be empty");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("Invalid file name");
        }

        String contentType = file.getContentType();

        if (!"application/pdf".equals(contentType)
                && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            throw new RuntimeException("Only PDF and DOCX files are allowed");
        }

        int version = resumeService.getNextVersion(user);

        String objectKey =
                "users/" + user.getId() + "/resumes/"
                        + System.currentTimeMillis() + "_" + originalFileName;

        storageService.uploadFile(objectKey, file);

        Resume resume = new Resume();
        resume.setName(name);
        resume.setVersion(version);
        resume.setFileName(originalFileName);

        // Kept only for compatibility with existing database records.
        resume.setFilePath("");

        // Backblaze B2 object key
        resume.setStorageKey(objectKey);

        resume.setUser(user);

        Resume savedResume = resumeService.saveResume(resume);

        return toResponse(savedResume);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        User user = userService.getUserEntityByEmail(
                authentication.getName()
        );

        Resume resume = resumeService.getUserResume(id, user);

        Resource resource;

        // New resumes stored in Backblaze B2
        if (resume.getStorageKey() != null
                && !resume.getStorageKey().isBlank()) {

            InputStream inputStream =
                    storageService.downloadFile(resume.getStorageKey());

            resource = new InputStreamResource(inputStream);

        } else {
            // Existing resumes stored locally
            Path filePath = Paths.get(resume.getFilePath());

            resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Resume file not found");
            }
        }

        String contentType =
                resume.getFileName().toLowerCase().endsWith(".pdf")
                        ? "application/pdf"
                        : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resume.getFileName() + "\""
                )
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        contentType
                )
                .body(resource);
    }

    private ResumeResponse toResponse(Resume resume) {

        ResumeResponse response = new ResumeResponse();

        response.setId(resume.getId());
        response.setName(resume.getName());
        response.setVersion(resume.getVersion());
        response.setFileName(resume.getFileName());
        response.setCreatedAt(resume.getCreatedAt());
        response.setUpdatedAt(resume.getUpdatedAt());

        return response;
    }
}
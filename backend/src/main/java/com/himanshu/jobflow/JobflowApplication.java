package com.himanshu.jobflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobflowApplication {

	public static void main(String[] args) {

		SpringApplication.run(JobflowApplication.class, args);
	}

}


/*
For Test
POST
Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:8080/api/users/login" -Method POST -ContentType "application/json" -Body '{"email":"bob@example.com","password":"password123"}'

eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJib2JAZXhhbXBsZS5jb20iLCJpYXQiOjE3ODgyNzA2MTUsImV4cCI6MTc4ODI3NDIxNX0.M_LExZ2K4Yq_xKH9yF7Ww_7NccewxeArvON16bpYYyGlTLVlpJ6QBm8tx6aLK5lN


 */

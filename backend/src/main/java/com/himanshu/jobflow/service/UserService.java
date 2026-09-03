package com.himanshu.jobflow.service;

import com.himanshu.jobflow.dto.UserLoginRequest;
import com.himanshu.jobflow.dto.UserResponse;
import com.himanshu.jobflow.exception.EmailAlreadyExistsException;
import com.himanshu.jobflow.exception.InvalidCredentialsException;
import com.himanshu.jobflow.repository.UserRepository;
import com.himanshu.jobflow.dto.UserRegistrationRequest;
import com.himanshu.jobflow.entity.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerUser(UserRegistrationRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public String loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public User getUserEntityByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}

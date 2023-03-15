package com.amuse.www.demo.controller;

import com.amuse.www.demo.domain.dto.request.SignUpRequest;
import com.amuse.www.demo.exception.ValidationException;
import com.amuse.www.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/**")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<?> signUpNewUser(@RequestBody @Valid SignUpRequest signUpRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()) throw new ValidationException("회원가입 유효성 검사 실패.", bindingResult.getFieldErrors());
        userService.saveUser(signUpRequest);
        return ResponseEntity.ok("Success");
    }
}

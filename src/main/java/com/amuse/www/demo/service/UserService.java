package com.amuse.www.demo.service;

import com.amuse.www.demo.domain.dto.request.SignUpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public interface UserService {

    @Transactional
    void saveUser(SignUpRequest signUpRequest);
}

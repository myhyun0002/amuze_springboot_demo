package com.amuse.www.demo.service.Impl;

import com.amuse.www.demo.domain.dto.request.SignUpRequest;
import com.amuse.www.demo.domain.item.User;
import com.amuse.www.demo.domain.item.UserType;
import com.amuse.www.demo.exception.DuplicateUserException;
import com.amuse.www.demo.exception.SimpleFieldError;
import com.amuse.www.demo.repository.UserRepository;
import com.amuse.www.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void saveUser(SignUpRequest signUpRequest){
        checkDuplicateEmail(signUpRequest.getEmail());
        User user = User.builder()
                .username(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .type(UserType.DEFAULT)
                .build();

        userRepository.save(user);
    }

    // 같은 이메일을 사용하고 있는 유저가 있는지 확인
    private void checkDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateUserException("사용중인 이메일 입니다.", new SimpleFieldError("email", "사용중인 이메일 입니다."));
    }
}

package com.amuse.www.demo.controller;

import com.amuse.www.demo.config.CookieUtils;
import com.amuse.www.demo.config.StatelessCSRFFilter;
import com.amuse.www.demo.domain.dto.request.AuthorizationRequest;
import com.amuse.www.demo.exception.AuthenticationFailedException;
import com.amuse.www.demo.exception.ValidationException;
import com.amuse.www.demo.jwt.JwtProvider;
import com.amuse.www.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @GetMapping("/csrf-token")
    public ResponseEntity<?> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        String csrfToken = UUID.randomUUID().toString();

        Map<String, String> resMap = new HashMap<>();
        resMap.put(StatelessCSRFFilter.CSRF_TOKEN, csrfToken);

        generateCSRFTokenCookie(response);
        return ResponseEntity.ok(resMap);
    }

    private void generateCSRFTokenCookie(HttpServletResponse response) {
        CookieUtils.addCookie(response, StatelessCSRFFilter.CSRF_TOKEN, UUID.randomUUID().toString(), 60 * 60 * 24);
    }


    /* 사용자의 계정을 인증하고 로그인 토큰을 발급해주는 컨트롤러 */
    @PostMapping("/authorize")
    public void authenticateUsernamePassword(@Valid @RequestBody AuthorizationRequest authorizationRequest, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (bindingResult.hasErrors()) throw new ValidationException("로그인 유효성 검사 실패.", bindingResult.getFieldErrors());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authorizationRequest.getUsername(), authorizationRequest.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            generateTokenCookie(userDetails, request, response);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("아이디 또는 패스워드가 틀렸습니다.");
        }
    }

    private void generateTokenCookie(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        final int cookieMaxAge = jwtProvider.getTokenExpirationDate().intValue();
        //https 프로토콜인 경우 secure 옵션사용
        boolean secure = request.isSecure();
        CookieUtils.addCookie(response, "access_token", jwtProvider.generateToken(userDetails.getUsername()), true, secure, cookieMaxAge);
    }

    // 로그아웃할 때는 RSFP 토큰과 ACCESS TOKEN 둘다 삭제
    @PostMapping("/logout")
    public ResponseEntity<?> expiredToken(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "access_token");
        CookieUtils.deleteCookie(request, response, StatelessCSRFFilter.CSRF_TOKEN);
        return ResponseEntity.ok("success");
    }
}

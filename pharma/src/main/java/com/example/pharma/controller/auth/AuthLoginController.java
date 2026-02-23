package com.example.pharma.controller.auth;

import com.example.pharma.dto.auth.login.LoginStartRequest;
import com.example.pharma.dto.auth.login.LoginStartResponse;
import com.example.pharma.dto.auth.login.LoginVerifyRequest;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.model.entity.core.User;
import com.example.pharma.security.jwt.JwtService;
import com.example.pharma.service.auth_services.AuthLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthLoginController {

    private final AuthLoginService loginService;
    private final JwtService jwtService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<LoginStartResponse>> start(
            @Valid @RequestBody LoginStartRequest request) {

        LoginStartResponse response = loginService.start(request);

        return ResponseEntity.ok(ApiResponse.success("",response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody LoginVerifyRequest request,
            HttpServletResponse response) {

        User user = loginService.verify(request);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(
                        user.getEmail(),
                        null,
                        AuthorityUtils.createAuthorityList(
                                user.getRoles()
                                        .stream()
                                        .map(Enum::name)
                                        .toArray(String[]::new)
                        )
                );

        String jwt = jwtService.generateToken(auth);

        response.setHeader("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok().build();
    }
}

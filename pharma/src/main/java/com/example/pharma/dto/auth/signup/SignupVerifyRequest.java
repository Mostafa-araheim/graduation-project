package com.example.pharma.dto.auth.signup;

public record SignupVerifyRequest(

        String signupId,

        String code

) {}

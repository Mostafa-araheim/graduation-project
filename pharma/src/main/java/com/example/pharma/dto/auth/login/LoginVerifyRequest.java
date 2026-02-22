package com.example.pharma.dto.auth.login;

public record LoginVerifyRequest(


        String loginId,

        String code

) {
}

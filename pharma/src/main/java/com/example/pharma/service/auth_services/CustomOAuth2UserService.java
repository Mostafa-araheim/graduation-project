package com.example.pharma.service.auth_services;

import com.example.pharma.model.auth.AuthProvider;
import com.example.pharma.model.auth.Provider;
import com.example.pharma.model.core.User;
import com.example.pharma.model.core.UserRole;
import com.example.pharma.repository.Auth.AuthProviderRepository;
import com.example.pharma.repository.Core.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepo;
    private final AuthProviderRepository providerRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerUserId = oAuth2User.getName();

        AuthProvider existing =
                providerRepo.findByProviderAndProviderUserId(
                        Provider.GOOGLE, providerUserId
                ).orElse(null);

        if (existing != null) {
            return oAuth2User;
        }

        User user = userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setName(name);
                    u.setEmail(email);
                    u.setRole(UserRole.CUSTOMER);
                    u.setEmailVerified(true);
                    return userRepo.save(u);
                });

        AuthProvider provider = new AuthProvider();
        provider.setUser(user);
        provider.setProvider(Provider.GOOGLE);
        provider.setProviderUserId(providerUserId);

        providerRepo.save(provider);

        return oAuth2User;
    }

}

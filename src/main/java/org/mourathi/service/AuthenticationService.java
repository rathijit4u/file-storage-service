package org.mourathi.service;

import jakarta.servlet.http.HttpServletRequest;
import org.mourathi.auth.ApiKeyAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private static final String AUTH_TOKEN = "API_KEY";

    @Autowired
    IApiKeyService apiKeyService;

    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !apiKey.equals(System.getenv(AUTH_TOKEN))) {
            throw new BadCredentialsException("Invalid API Key");
        }
        apiKeyService.findApiKey(apiKey);
        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
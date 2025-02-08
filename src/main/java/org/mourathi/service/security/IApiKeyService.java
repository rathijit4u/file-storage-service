package org.mourathi.service.security;

import org.mourathi.model.APIKey;

public interface IApiKeyService {
    APIKey findApiKey(String apiKey);
}

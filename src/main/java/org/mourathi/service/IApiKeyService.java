package org.mourathi.service;

import org.mourathi.model.APIKey;

public interface IApiKeyService {
    APIKey findApiKey(String apiKey);
}

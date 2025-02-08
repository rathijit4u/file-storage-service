package org.mourathi.service.security;

import org.mourathi.model.APIKey;
import org.mourathi.repository.ApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService implements IApiKeyService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ApiRepository apiRepository;

    @Override
    public APIKey findApiKey(String apiKey){
        return apiRepository.findByKey(apiKey);
    }
}

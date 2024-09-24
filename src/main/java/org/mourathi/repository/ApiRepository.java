package org.mourathi.repository;

import org.mourathi.model.APIKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository extends CrudRepository<APIKey, String> {
    APIKey findByKey(String key);
}

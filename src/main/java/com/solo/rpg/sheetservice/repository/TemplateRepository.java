package com.solo.rpg.sheetservice.repository;

import com.solo.rpg.sheetservice.model.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {
    Template findBySystemName(String systemName);
}

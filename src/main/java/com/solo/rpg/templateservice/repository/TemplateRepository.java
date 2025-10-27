package com.solo.rpg.templateservice.repository;

import com.solo.rpg.templateservice.model.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {
    Template findBySystemName(String systemName);
    List<Template> findAllByOwnerId(String ownerId);
}

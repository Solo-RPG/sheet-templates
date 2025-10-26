package com.solo.rpg.sheetservice.service;

import com.solo.rpg.sheetservice.model.Template;
import com.solo.rpg.sheetservice.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository repository;

    public Template createTemplate(Template template,  String userId) {
        template.setOwnerId(userId);
        return repository.save(template);
    }

    public Template getTemplateById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Template getTemplateByName(String systemName) {
        return repository.findBySystemName(systemName);
    }

    public List<Template> getAllTemplates() {
        return repository.findAll();
    }

    public List<Template> getAllTemplatesById(String id) {
        return repository.findAllByOwnerId(id);
    }

    public void delete(String systemName) {
        Template template = repository.findBySystemName(systemName);
        if (template != null) {
            repository.delete(template);
        }
    }
}

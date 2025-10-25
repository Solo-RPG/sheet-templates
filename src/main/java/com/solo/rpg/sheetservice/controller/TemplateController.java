package com.solo.rpg.sheetservice.controller;


import com.solo.rpg.sheetservice.model.Template;
import com.solo.rpg.sheetservice.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService service;

    @GetMapping("/")
    private ResponseEntity<List<Template>> getTemplates() {
        return ResponseEntity.ok(service.getAllTemplates());
    }

    @GetMapping("/by-name/{systemName}")
    private ResponseEntity<Template> getTemplatesByName(@PathVariable String systemName) {
        return ResponseEntity.ok(service.getTemplateByName(systemName));
    }

    @GetMapping("/by-id/{id}")
    private ResponseEntity<Template> getTemplatesById(@PathVariable String id) {
        Template template = service.getTemplateById(id);

        if (template == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(template);
    }

    @GetMapping("/{systemName}/fields")
    private ResponseEntity<Object> getFields(@PathVariable String systemName) {


        Template template = service.getTemplateByName(systemName);

        if (template == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(template.getFields());
    }

    @PostMapping("/")
    public ResponseEntity<Template> createTemplate(@RequestBody Template template) {

        try {
            return ResponseEntity.ok(service.createTemplate(template));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{systemName}")
    public Object deleteTemplate(@PathVariable String systemName) {

        service.delete(systemName);

        if (service.getTemplateByName(systemName) == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).body("Failed to delete template");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable String id, @RequestBody Template updatedTemplate) {


        Template existingTemplate = service.getTemplateById(id);

        if (existingTemplate == null) {
            return ResponseEntity.notFound().build();
        }

        deleteTemplate(existingTemplate.getSystemName());
        updatedTemplate.setId(existingTemplate.getId());

        return ResponseEntity.ok(service.createTemplate(updatedTemplate));
    }
}

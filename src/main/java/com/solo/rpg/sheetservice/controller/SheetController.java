package com.solo.rpg.sheetservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.rpg.sheetservice.infraestructure.TemplateApiClient;
import com.solo.rpg.sheetservice.model.SheetCreateRequest;
import com.solo.rpg.sheetservice.model.SheetForm;
import com.solo.rpg.sheetservice.repository.SheetRepository;
import com.solo.rpg.sheetservice.service.SheetService;
import io.jsonwebtoken.Claims;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/sheets")

public class SheetController {

    @Autowired
    private SheetService service;

    @Autowired
    private SheetRepository repository;

    @GetMapping("/")
    private Object getSheets() {
        List<SheetForm> sheets = repository.findAll();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        if(sheets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return sheets;
    }

    @PostMapping("/")
    public ResponseEntity<SheetForm> createSheet(@RequestBody SheetCreateRequest sheet) {
        ObjectMapper mapper = new ObjectMapper();
        TemplateApiClient client = new TemplateApiClient(new RestTemplate());

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
            }

            Claims claims = (Claims) auth.getPrincipal();
            String userId = claims.get("userId", String.class);

            SheetCreateRequest request = mapper.convertValue(sheet, SheetCreateRequest.class);

            JSONObject template = null;

            if(request.getTemplateId().isEmpty())  {
                template = client.fetchTemplate(request.getSystemName(), false);
            } else {
                template = client.fetchTemplate(request.getTemplateId(), true);
            }

            SheetForm form = service.createSheetFromTemplate(request, template, userId);

            if(form == null) {
                return ResponseEntity.status(500).build();
            }

            return ResponseEntity.ok().body(form);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    private Object getSheet(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        SheetForm sheet = repository.findById(id).orElse(null);

        if(sheet == null) {
            return ResponseEntity.noContent().build();
        }

        return sheet;
    }

    @GetMapping("/by-user_id/{id}")
    private Object getSheetByUserId(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        SheetForm sheet = repository.findByOwnerId(id).orElse(null);

        if(sheet == null) {
            return ResponseEntity.noContent().build();
        }

        return sheet;
    }

    @GetMapping("/by-name/{id}")
    private Object getTemplateByName(@PathVariable String id) {
        TemplateApiClient client = new TemplateApiClient(new RestTemplate());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        JSONObject object = client.getTemplateByName(id);

        if(object == null) {
            return ResponseEntity.noContent().build();
        }

        return object;
    }


    @DeleteMapping("/{id}")
    private Object deleteSheet(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        repository.deleteById(id);

        if(!repository.existsById(id)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(500).build();
    }

    @PutMapping("/{id}")
    private Object updateSheet(@PathVariable String id, @RequestBody SheetCreateRequest sheet) {
        SheetForm existingSheet = repository.findById(id).orElse(null);

        if(existingSheet == null) {
            return ResponseEntity.noContent().build();
        }
        existingSheet.setData(new JSONObject(sheet.getFields()));

        repository.save(existingSheet);

        return ResponseEntity.ok().body(existingSheet);
    }

    @GetMapping("/templates")
    private Object getTemplates() {
        TemplateApiClient client = new TemplateApiClient(new RestTemplate());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        Object objects = client.getTemplates();

        if(objects == null) {
            return ResponseEntity.noContent().build();
        }

        return objects;
    }
}

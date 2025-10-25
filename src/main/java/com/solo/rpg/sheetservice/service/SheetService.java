package com.solo.rpg.sheetservice.service;

import com.solo.rpg.sheetservice.model.SheetCreateRequest;
import com.solo.rpg.sheetservice.model.SheetForm;
import com.solo.rpg.sheetservice.repository.SheetRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SheetService {

    @Autowired
    private SheetRepository repository;

    public SheetForm createSheet(SheetForm sheetForm) {
       repository.save(sheetForm);
       return sheetForm;
    }

    public SheetForm createSheetFromTemplate(SheetCreateRequest request, JSONObject template, String userId) {
        SheetForm sheetForm = new SheetForm(
                UUID.randomUUID().toString(),
                request.getTemplateId(),
                template.getAsString("system_name"),
                template.getAsString("version"),
                userId,
                null,
                buildSheetData(
                        request.getFields(),
                        (List<Map<String, Object>>) template.get("fields"),
                        ""
                )
        );
        repository.save(sheetForm);
        return sheetForm;
    }

    private JSONObject buildSheetData(Map<String, Object> userData, List<Map<String, Object>> templateFields, String parentPath) {
        JSONObject sheetData = new JSONObject();

        for (Map<String, Object> field : templateFields) {
            String fieldName = (String) field.get("name");
            String currentPath = parentPath.isEmpty() ? fieldName : parentPath + "." + fieldName;
            boolean required = field.getOrDefault("required", true).equals(true);

            if (!userData.containsKey(fieldName)) {
                if (required) throw new IllegalArgumentException("Campo obrigatório faltando: " + currentPath);
                continue;
            }

            Object value = userData.get(fieldName);

            if (field.containsKey("fields") && value instanceof Map) {
                Map<String, Object> nestedUserData = (Map<String, Object>) value;
                List<Map<String, Object>> nestedTemplateFields = (List<Map<String, Object>>) field.get("fields");
                JSONObject nestedData = buildSheetData(nestedUserData, nestedTemplateFields, currentPath);
                sheetData.put(fieldName, nestedData);
            } else {
                validateField(value, field, currentPath);
                sheetData.put(fieldName, value);
            }
        }

        return sheetData;
    }


    private void validateField(Object value, Map<String, Object> templateField, String fieldPath) {
        String expectedType = (String) templateField.get("type");

        if ("number".equals(expectedType) && !(value instanceof Number)) {
            throw new IllegalArgumentException("Campo " + fieldPath + " deve ser um número");
        } else if ("string".equals(expectedType) && !(value instanceof String)) {
            throw new IllegalArgumentException("Campo " + fieldPath + " deve ser uma string");
        }

        @SuppressWarnings("unchecked")
        List<Object> options = (List<Object>) templateField.get("options");

        if (options != null && !options.isEmpty() && !options.contains(value)) {
            throw new IllegalArgumentException("Valor inválido para " + fieldPath + ". Opções: " + options);
        }
    }
}

package com.solo.rpg.sheetservice.infraestructure;

import com.solo.rpg.sheetservice.config.JwtTokenInterceptor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TemplateApiClient {

    private final RestTemplate restTemplate;
    private final String url = "http://localhost:7000/api/templates/";

    public TemplateApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders() {
        JwtTokenInterceptor interceptor = new JwtTokenInterceptor();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + interceptor.extractTokenFromRequest());
        return headers;
    }

    public JSONObject getTemplates() {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Template não encontrado");
        }

        return new JSONObject(response);
    }

    public JSONObject getTemplateById(String templateId) {
        String endpoint = url + "by-id/" + templateId;
        HttpEntity entity = new HttpEntity<>(createHeaders());

        ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.GET,entity, Map.class);


        if (response.getBody() == null) {
            throw new IllegalArgumentException("Template não encontrado");
        }

        return new JSONObject(response.getBody());
    }

    public JSONObject getTemplateByName(String templateName) {
        String endpoint = url + "by-name/" + templateName;
        Map<String, Object> response = restTemplate.getForObject(endpoint, Map.class);

        if (response == null) {
            throw new IllegalArgumentException("Template não encontrado");
        }

        return new JSONObject(response);
    }

    public JSONObject fetchTemplate(String name, boolean isId) {
        return isId ? getTemplateById(name) : getTemplateByName(name);
    }
}
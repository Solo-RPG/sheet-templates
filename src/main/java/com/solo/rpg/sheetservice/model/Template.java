package com.solo.rpg.sheetservice.model;

import net.minidev.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class Template {
    private String id;
    private String systemName;
    private String version;
    private List<TemplateField> fields;
    private JSONObject templateJson;

    public Template() {
        this.id = UUID.randomUUID().toString();
    }

    public Template(String systemName, String version, List<TemplateField> fields, JSONObject templateJson) {
        this.id = UUID.randomUUID().toString();
        this.systemName = systemName;
        this.version = version;
        this.fields = fields;
        this.templateJson = templateJson;
    }

    public String getSystemName() {
        return systemName;
    }
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public List<TemplateField> getFields() {
        return fields;
    }
    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }
    public JSONObject getTemplateJson() {
        return templateJson;
    }
    public void setTemplateJson(JSONObject templateJson) {
        this.templateJson = templateJson;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}

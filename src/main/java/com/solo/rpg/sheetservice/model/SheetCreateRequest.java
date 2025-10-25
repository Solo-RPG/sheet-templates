package com.solo.rpg.sheetservice.model;

import java.util.Map;

public class SheetCreateRequest {
    private String templateId;
    private String systemName;
    private String ownerId;
    private Map<String, Object> fields;

    public SheetCreateRequest() {
    }

    public SheetCreateRequest(String templateId, String systemName, String ownerId, Map<String, Object> data) {
        this.templateId = templateId;
        this.systemName = systemName;
        this.ownerId = ownerId;
        this.fields = data;
    }

    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    public String getSystemName() {
        return systemName;
    }
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public Map<String, Object> getFields() {
        return fields;
    }
    public void setFields(Map<String, Object> data) {
        this.fields = data;
    }

}

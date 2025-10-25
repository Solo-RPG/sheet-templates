package com.solo.rpg.sheetservice.model;

import net.minidev.json.JSONObject;

public class SheetForm {
    private String id;
    private String templateId;
    private String templateSystemName;
    private String templateSystemVersion;
    private String ownerId;
    private String characterId;
    private JSONObject data;

    public SheetForm(String id, String templateId, String templateSystemName, String templateSystemVersion, String ownerId, String characterId, JSONObject data) {
        this.id = id;
        this.templateId = templateId;
        this.templateSystemName = templateSystemName;
        this.templateSystemVersion = templateSystemVersion;
        this.ownerId = ownerId;
        this.characterId = characterId;
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }
    public void setData(JSONObject data) {
        this.data = data;
    }
    public String getCharacterId() {
        return characterId;
    }
    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getTemplateSystemVersion() {
        return templateSystemVersion;
    }
    public void setTemplateSystemVersion(String templateSystemVersion) {
        this.templateSystemVersion = templateSystemVersion;
    }
    public String getTemplateSystemName() {
        return templateSystemName;
    }
    public void setTemplateSystemName(String templateSystemName) {
        this.templateSystemName = templateSystemName;
    }
    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


}

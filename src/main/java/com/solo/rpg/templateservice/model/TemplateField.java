package com.solo.rpg.templateservice.model;

import java.util.List;

public class TemplateField {
    private String name;
    private String type;
    private boolean required;
    private String defaultValue;
    private String flex;
    private String span;
    private String cols;
    private String color;
    private List<String> options;
    private List<TemplateField> fields;

    public TemplateField() {
    }

    public TemplateField(String name, String type, boolean required, String defaultValue, List<String> options, List<TemplateField> fields, String flex, String span, String cols, String color) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
        this.flex = flex;
        this.options = options;
        this.fields = fields;
        this.span = span;
        this.cols = cols;
        this.color = color;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlex() { return flex; }

    public void setFlex(String flex) { this.flex = flex; }

    public String getSpan() { return span; }

    public void setSpan(String span) { this.span = span; }

    public String getCols() { return cols;}

    public void setCols(String cols) { this.cols = cols; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }
}

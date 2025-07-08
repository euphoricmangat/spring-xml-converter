package com.springconverter.model;

/**
 * Represents a Spring bean property definition.
 */
public class Property {
    private String name;
    private String value;
    private String ref;
    private String type;
    private boolean isList;
    private boolean isMap;
    private boolean isSet;

    public Property() {
    }

    public Property(String name) {
        this.name = name;
    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Property(String name, String ref, boolean isRef) {
        this.name = name;
        if (isRef) {
            this.ref = ref;
        } else {
            this.value = ref;
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean map) {
        isMap = map;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public boolean isReference() {
        return ref != null && !ref.isEmpty();
    }

    public boolean isValue() {
        return value != null && !value.isEmpty();
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", ref='" + ref + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
} 
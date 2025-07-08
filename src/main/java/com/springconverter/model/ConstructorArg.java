package com.springconverter.model;

/**
 * Represents a Spring constructor argument definition.
 */
public class ConstructorArg {
    private String value;
    private String ref;
    private String type;
    private int index;
    private String name;

    public ConstructorArg() {
    }

    public ConstructorArg(String value) {
        this.value = value;
    }

    public ConstructorArg(String ref, boolean isRef) {
        if (isRef) {
            this.ref = ref;
        } else {
            this.value = ref;
        }
    }

    // Getters and Setters
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReference() {
        return ref != null && !ref.isEmpty();
    }

    public boolean isValue() {
        return value != null && !value.isEmpty();
    }

    @Override
    public String toString() {
        return "ConstructorArg{" +
                "value='" + value + '\'' +
                ", ref='" + ref + '\'' +
                ", type='" + type + '\'' +
                ", index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
} 
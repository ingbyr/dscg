package com.ingbyr.hwsc.common;

/**
 * The class represents semantic thing in taxonomy document
 */
public class Thing extends NamedObject {

    private String type; // Parent class

    public Thing(String name) {
        super(name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

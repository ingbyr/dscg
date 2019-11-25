package com.ingbyr.hwsc.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The class represents an object with a unique name
 */
@AllArgsConstructor
@Getter
@Setter
public abstract class NamedObject {
    protected String name;

    @Override
    public String toString() {
        return name;
    }
}

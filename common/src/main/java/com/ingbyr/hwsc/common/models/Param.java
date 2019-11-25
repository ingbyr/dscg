package com.ingbyr.hwsc.common.models;

/**
 * This class represents web service parameter
 */
public class Param extends NamedObject {

    private Thing thing;

    public Param(String name) {
        super(name);
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }


}

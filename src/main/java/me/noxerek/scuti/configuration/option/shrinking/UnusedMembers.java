package me.noxerek.scuti.configuration.option.shrinking;

import me.noxerek.scuti.configuration.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author netindev
 */
public class UnusedMembers extends Option {

    private List<String> keepClasses;
    private boolean classes, methods, fields;

    /* init */ {
        this.setKeepClasses(new ArrayList<>());
    }

    public List<String> getKeepClasses() {
        return this.keepClasses;
    }

    public void setKeepClasses(final List<String> keepClasses) {
        this.keepClasses = keepClasses;
    }

    public boolean isClasses() {
        return this.classes;
    }

    public void setClasses(final boolean classes) {
        this.classes = classes;
    }

    public boolean isMethods() {
        return this.methods;
    }

    public void setMethods(final boolean methods) {
        this.methods = methods;
    }

    public boolean isFields() {
        return this.fields;
    }

    public void setFields(final boolean fields) {
        this.fields = fields;
    }

}

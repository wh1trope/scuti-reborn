package me.noxerek.scuti.configuration.option.obfuscation;

import me.noxerek.scuti.configuration.Option;

/**
 * @author netindev
 */
public class NumberObfuscation extends Option {

    private boolean executeTwice;

    public boolean isExecuteTwice() {
        return this.executeTwice;
    }

    public void setExecuteTwice(final boolean executeTwice) {
        this.executeTwice = executeTwice;
    }

}

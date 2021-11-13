package me.noxerek.scuti.dictionary.type;

import me.noxerek.scuti.dictionary.Dictionary;

/**
 * @author netindev
 */
public class Number implements Dictionary {

    private int count;

    @Override
    public String next() {
        return String.valueOf(this.count++);
    }

    @Override
    public void reset() {
        this.count = 0;
    }

}

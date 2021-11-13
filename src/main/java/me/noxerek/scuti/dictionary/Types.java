package me.noxerek.scuti.dictionary;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.dictionary.type.Alphabet;
import me.noxerek.scuti.dictionary.type.Custom;
import me.noxerek.scuti.dictionary.type.Number;
import me.noxerek.scuti.dictionary.type.Randomized;

/**
 * @author netindev
 */
public enum Types {

    ALPHABET, NUMBER, RANDOMIZED, CUSTOM;

    public static Dictionary getDictionary(final Configuration configuration, final CustomDictionary customDictionary) {
        switch (configuration.getRenameMembers().getRandomize()) {
            case NUMBER:
                return new Number();
            case RANDOMIZED:
                return new Randomized();
            case CUSTOM:
                switch (customDictionary) {
                    case CLASSES:
                        return new Custom(configuration.getRenameMembers().getClassesDictionary());
                    case FIELDS:
                        return new Custom(configuration.getRenameMembers().getFieldsDictionary());
                    case METHODS:
                        return new Custom(configuration.getRenameMembers().getMethodsDictionary());
                    case PACKAGES:
                        return new Custom(configuration.getRenameMembers().getPackagesDictionary());
                }
            default:
                return new Alphabet();
        }
    }

    public enum CustomDictionary {
        CLASSES, FIELDS, METHODS, PACKAGES
    }

}

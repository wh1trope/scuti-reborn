package me.noxerek.scuti.configuration.option.obfuscation;

import me.noxerek.scuti.configuration.Option;

/**
 * @author netindev
 */
public class StringEncryption extends Option {

    private EncryptionType encryptionType;

    /* default config */ {
        this.setEncryptionType(EncryptionType.FAST);
    }

    public EncryptionType getEncryptionType() {
        return this.encryptionType;
    }

    public void setEncryptionType(final EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public enum EncryptionType {
        FAST, STRONG
    }

}

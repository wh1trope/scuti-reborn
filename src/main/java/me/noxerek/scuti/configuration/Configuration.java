package me.noxerek.scuti.configuration;

import me.noxerek.scuti.configuration.option.obfuscation.*;
import me.noxerek.scuti.configuration.option.shrinking.UnusedMembers;
import me.noxerek.scuti.transform.Transformer;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author netindev
 */
public interface Configuration {

    File getInput();

    File getOutput();

    List<File> getDependencies();

    Set<Class<? extends Transformer>> getTransformers();

    boolean corruptCRC32();

    boolean corruptNames();

    ClassEncrypt getClassEncrypt();

    MiscellaneousObfuscation getMiscellaneousObfuscation();

    NumberObfuscation getNumberObfuscation();

    RenameMembers getRenameMembers();

    StringEncryption getStringEncryption();

    UnusedMembers getUnusedMembers();

}

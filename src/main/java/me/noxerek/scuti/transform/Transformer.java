package me.noxerek.scuti.transform;

import me.noxerek.scuti.configuration.Configuration;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.Map;

/**
 * @author netindev
 */
public abstract class Transformer {

    private final Configuration configuration;
    private final Map<String, ClassNode> classes, dependencies;

    public Transformer(final Configuration configuration, final Map<String, ClassNode> classes,
                       final Map<String, ClassNode> dependencies) {
        this.configuration = configuration;
        this.classes = Collections.synchronizedMap(classes);
        this.dependencies = Collections.synchronizedMap(dependencies);
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

    protected Map<String, ClassNode> getClasses() {
        return this.classes;
    }

    protected Map<String, ClassNode> getDependencies() {
        return this.dependencies;
    }

    public abstract void transform();

}

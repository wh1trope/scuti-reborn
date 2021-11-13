package me.noxerek.scuti.transform.obfuscation;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.transform.Transformer;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author netindev
 */
public class ShuffleMembersTransformer extends Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringEncryptionTransformer.class.getName());

    public ShuffleMembersTransformer(final Configuration configuration, final Map<String, ClassNode> classes,
                                     final Map<String, ClassNode> dependencies) {
        super(configuration, classes, dependencies);
        LOGGER.info(" Shuffle Member Transformer ->");
    }

    private static void shuffleIfNonnull(List<?> list) {
        if (list != null) Collections.shuffle(list);
    }

    @Override
    public void transform() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        this.getClasses().values().forEach(classNode -> {
            shuffleIfNonnull(classNode.fields);
            shuffleIfNonnull(classNode.methods);
            shuffleIfNonnull(classNode.innerClasses);
            shuffleIfNonnull(classNode.interfaces);
            shuffleIfNonnull(classNode.attrs);
            shuffleIfNonnull(classNode.invisibleAnnotations);
            shuffleIfNonnull(classNode.visibleAnnotations);
            shuffleIfNonnull(classNode.invisibleTypeAnnotations);
            shuffleIfNonnull(classNode.visibleTypeAnnotations);
            classNode.fields.forEach(f -> {
                shuffleIfNonnull(f.attrs);
                shuffleIfNonnull(f.invisibleAnnotations);
                shuffleIfNonnull(f.visibleAnnotations);
                shuffleIfNonnull(f.invisibleTypeAnnotations);
                shuffleIfNonnull(f.visibleTypeAnnotations);
                atomicInteger.addAndGet(classNode.fields.size());
            });
            classNode.methods.forEach(m -> {
                shuffleIfNonnull(m.attrs);
                shuffleIfNonnull(m.invisibleAnnotations);
                shuffleIfNonnull(m.visibleAnnotations);
                shuffleIfNonnull(m.invisibleTypeAnnotations);
                shuffleIfNonnull(m.visibleTypeAnnotations);
                shuffleIfNonnull(m.exceptions);
                shuffleIfNonnull(m.invisibleLocalVariableAnnotations);
                shuffleIfNonnull(m.visibleLocalVariableAnnotations);
                shuffleIfNonnull(m.localVariables);
                shuffleIfNonnull(m.parameters);
                atomicInteger.addAndGet(classNode.methods.size());
            });
        });
        LOGGER.info(" - Shuffled " + atomicInteger.get() + " members");
    }

}

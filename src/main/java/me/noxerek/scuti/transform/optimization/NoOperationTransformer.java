package me.noxerek.scuti.transform.optimization;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.transform.Transformer;
import me.noxerek.scuti.transform.obfuscation.ClassEncryptTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author netindev
 */
public class NoOperationTransformer extends Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassEncryptTransformer.class.getName());

    public NoOperationTransformer(final Configuration configuration, final Map<String, ClassNode> classes,
                                  final Map<String, ClassNode> dependencies) {
        super(configuration, classes, dependencies);
        LOGGER.info(" No Operation Transformer ->");
    }

    @Override
    public void transform() {
        final AtomicInteger insnCount = new AtomicInteger();
        this.getClasses().values().forEach(
                classNode -> classNode.methods.forEach(methodNode -> Arrays.stream(methodNode.instructions.toArray())
                        .filter(insnNode -> insnNode.getOpcode() == Opcodes.NOP).forEach(insnNode -> {
                            insnCount.incrementAndGet();
                            methodNode.instructions.remove(insnNode);
                        })));
        if (insnCount.get() > 0) {
            LOGGER.info(" - Removed " + insnCount.get() + " instructions");
        }
    }

}

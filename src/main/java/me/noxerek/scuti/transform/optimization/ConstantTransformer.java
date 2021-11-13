package me.noxerek.scuti.transform.optimization;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.transform.Transformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * @author netindev
 */
public class ConstantTransformer extends Transformer {

    public ConstantTransformer(final Configuration configuration, final Map<String, ClassNode> classes,
                               final Map<String, ClassNode> dependencies) {
        super(configuration, classes, dependencies);
    }

    @Override
    public void transform() {

    }

}

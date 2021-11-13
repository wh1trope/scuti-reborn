package me.noxerek.scuti.transform.obfuscation;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.transform.Transformer;
import me.noxerek.scuti.util.ASMUtil;
import me.noxerek.scuti.util.RandomUtil;
import me.noxerek.scuti.util.StringUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

/**
 * @author netindev
 */
public class ControlFlowTransformer extends Transformer implements Opcodes {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlFlowTransformer.class.getName());

    private final FieldNode negativeField = new FieldNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC,
            StringUtil.getMassiveString().substring((int) (Byte.MAX_VALUE / 2.51)), "I", null, null);
    private final FieldNode positiveField = new FieldNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC,
            StringUtil.getMassiveString().substring((int) (Byte.MAX_VALUE / 2.49)), "I", null, null);

    int[] conditions = {Opcodes.IF_ICMPLT, Opcodes.IF_ICMPLE, Opcodes.IF_ICMPNE};

    public ControlFlowTransformer(final Configuration configuration, final Map<String, ClassNode> classes,
                                  final Map<String, ClassNode> dependencies) {
        super(configuration, classes, dependencies);
    }

    @Override
    public void transform() {
        LOGGER.info(" Executing control flow (experimental)");
        this.getClasses().values().stream()
                .filter(classNode -> !Modifier.isInterface(classNode.access)
                        && !this.getConfiguration().getClassEncrypt().getLoaderName().equals(classNode.name))
                .forEach(classNode -> {
                    classNode.methods.stream().filter(methodNode -> !Modifier.isAbstract(methodNode.access)
                            && !Modifier.isNative(methodNode.access)).forEach(methodNode -> {
                        Arrays.stream(methodNode.instructions.toArray()).forEach(insnNode -> {
                            if (insnNode instanceof LabelNode && (insnNode.getOpcode() != Opcodes.RETURN
                                    || insnNode.getOpcode() != Opcodes.ARETURN)) {
                                methodNode.instructions.insert(insnNode,
                                        this.getRandomConditionList(classNode));
                                final LabelNode labelAfter = new LabelNode();
                                final LabelNode labelBefore = new LabelNode();
                                final LabelNode labelFinal = new LabelNode();
                                methodNode.instructions.insertBefore(insnNode, labelBefore);
                                methodNode.instructions.insert(insnNode, labelAfter);
                                methodNode.instructions.insert(labelAfter, labelFinal);
                                methodNode.instructions.insert(labelBefore,
                                        new JumpInsnNode(Opcodes.GOTO, labelAfter));
                                methodNode.instructions.insert(labelAfter,
                                        new JumpInsnNode(Opcodes.GOTO, labelFinal));
                            }
                        });
                        this.heavyDoubleAthrow(classNode, methodNode);
                    });
                    final MethodNode staticInitializer = ASMUtil.getOrCreateClinit(classNode);

                    final InsnList insnList = new InsnList();
                    final int splitable = -RandomUtil.getRandom(Short.MAX_VALUE);
                    insnList.add(ASMUtil.toInsnNode(-splitable ^ 50 + RandomUtil.getRandom(Short.MAX_VALUE)));
                    insnList.add(ASMUtil.toInsnNode(splitable));
                    insnList.add(new InsnNode(Opcodes.IXOR));
                    insnList.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, this.negativeField.name, "I"));
                    insnList.add(ASMUtil.toInsnNode(splitable ^ 50 + RandomUtil.getRandom(Short.MAX_VALUE)));
                    insnList.add(ASMUtil.toInsnNode(splitable));
                    insnList.add(new InsnNode(Opcodes.IXOR));
                    insnList.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, this.positiveField.name, "I"));
                    staticInitializer.instructions.insert(insnList);
                });
    }

    private InsnList getRandomConditionList(final ClassNode classNode) {
        final InsnList insnList = new InsnList();
        /*
         * switch (RandomUtil.getRandom(6)) { default: final LabelNode startLabel = new
         * LabelNode(); insnList.add(new FieldInsnNode(GETSTATIC, classNode.name,
         * this.negativeField.name, "I")); insnList.add(new FieldInsnNode(GETSTATIC,
         * classNode.name, this.positiveField.name, "I")); insnList.add(new
         * JumpInsnNode(Opcodes.IF_ICMPLT, startLabel)); insnList.add(new
         * InsnNode(Opcodes.ACONST_NULL)); insnList.add(new InsnNode(Opcodes.ATHROW));
         * insnList.add(startLabel); break; }
         */
        final LabelNode startLabel = new LabelNode();
        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, this.negativeField.name, "I"));
        insnList.add(new FieldInsnNode(GETSTATIC, classNode.name, this.positiveField.name, "I"));
        insnList.add(new JumpInsnNode(Opcodes.IF_ICMPLT, startLabel));
        insnList.add(new InsnNode(Opcodes.ACONST_NULL));
        insnList.add(new InsnNode(Opcodes.ATHROW));
        insnList.add(startLabel);
        return insnList;
    }

    private void heavyDoubleAthrow(final ClassNode classNode, final MethodNode methodNode) {
        final InsnList insnlist = new InsnList();
        final LabelNode firstLabel = new LabelNode();
        final LabelNode secondLabel = new LabelNode();
        final LabelNode thirdLabel = new LabelNode();
        insnlist.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, this.negativeField.name, "I"));
        insnlist.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, this.positiveField.name, "I"));
        insnlist.add(new JumpInsnNode(this.conditions[RandomUtil.getRandom(this.conditions.length)], thirdLabel));
        insnlist.add(new InsnNode(Opcodes.ACONST_NULL));
        insnlist.add(firstLabel);
        insnlist.add(new InsnNode(Opcodes.ATHROW));
        insnlist.add(secondLabel);
        insnlist.add(new InsnNode(Opcodes.ATHROW));
        insnlist.add(thirdLabel);
        methodNode.instructions.insert(insnlist);
    }

}

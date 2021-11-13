package me.noxerek.scuti.transform.obfuscation;

import me.noxerek.scuti.configuration.Configuration;
import me.noxerek.scuti.transform.Transformer;
import me.noxerek.scuti.util.ASMUtil;
import me.noxerek.scuti.util.RandomUtil;
import me.noxerek.scuti.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author netindev
 */
public class MiscellaneousObfuscationTransformer extends Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiscellaneousObfuscationTransformer.class.getName());
    private static final String EMPTY_STRINGS;

    static {
        EMPTY_STRINGS = StringUtils.repeat("\n", 500);
    }

    private final List<ClassNode> randomExceptions = new ArrayList<>();

    public MiscellaneousObfuscationTransformer(final Configuration configuration, final Map<String, ClassNode> classes,
                                               final Map<String, ClassNode> dependencies) {
        super(configuration, classes, dependencies);
        LOGGER.info(" Miscellaneous Transformer ->");
    }

    @Override
    public void transform() {
        if (this.getConfiguration().getMiscellaneousObfuscation().isRandomExceptions()) {
            this.getClasses().values().forEach(classNode -> {
                if (Modifier.isPublic(classNode.access) && !classNode.name.contains("$")) {
                    randomExceptions.add(classNode);
                }
            });
        }
        this.getClasses().values().forEach(classNode -> {
            if (!Modifier.isInterface(classNode.access)) {
                if (this.getConfiguration().getMiscellaneousObfuscation().isPushTransient()) {
                    this.pushTransient(classNode);
                }
                if (this.getConfiguration().getMiscellaneousObfuscation().isPushVarargs() // && !this.getConfiguration()
                    //.getTransformers().contains(Transformers.Obfuscation.INVOKE_DYNAMIC_TRANSFORMER
                ) {
                    this.pushVarargs(classNode);
                }
                if (this.getConfiguration().getMiscellaneousObfuscation().isVariableDescritor()) {
                    this.randomDescriptor(classNode);
                }
                if (this.getConfiguration().getMiscellaneousObfuscation().isDuplicateVariables()) {
                    classNode.methods.forEach(methodNode -> {
                        this.popDupLoads(methodNode);
                        this.popSwapIntegers(methodNode);
                        this.popSwapLoads(methodNode);
                    });
                }
            }
            if (this.getConfiguration().getMiscellaneousObfuscation().isRandomExceptions()) {
                classNode.methods.forEach(this::randomExceptions);
            }
            if (this.getConfiguration().getMiscellaneousObfuscation().isMassiveSignature()) {
                this.massiveSignature(classNode);
            }
            if (this.getConfiguration().getMiscellaneousObfuscation().isMassiveSource()) {
                this.massiveSource(classNode);
            }
            if (this.getConfiguration().getMiscellaneousObfuscation().isInvalidAnnotation()) {
                this.invalidAnnotations(classNode);
            }
        });
    }

    private void randomExceptions(final MethodNode methodNode) {
        if (RandomUtil.getRandom()) {
            for (int i = 0; i < RandomUtil.getRandom(3); i++) {
                methodNode.exceptions.add(randomExceptions.get(RandomUtil.getRandom(randomExceptions.size())).name);
            }
        }
    }

    private void popDupLoads(final MethodNode methodNode) {
        Arrays.stream(methodNode.instructions.toArray()).filter(insnNode -> insnNode instanceof VarInsnNode)
                .forEach(insnNode -> {
                    if (insnNode.getOpcode() == Opcodes.ALOAD || insnNode.getOpcode() == Opcodes.FLOAD
                            || insnNode.getOpcode() == Opcodes.ILOAD) {
                        methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.POP2));
                        methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.DUP));
                        methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.DUP));
                    }
                });
    }

    private void popSwapLoads(final MethodNode methodNode) {
        Arrays.stream(methodNode.instructions.toArray()).filter(insnNode -> insnNode instanceof VarInsnNode)
                .forEach(insnNode -> {
                    if (insnNode.getOpcode() == Opcodes.ALOAD || insnNode.getOpcode() == Opcodes.FLOAD
                            || insnNode.getOpcode() == Opcodes.ILOAD) {
                        methodNode.instructions.insertBefore(insnNode,
                                new VarInsnNode(insnNode.getOpcode(), ((VarInsnNode) insnNode).var));
                        methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.POP));
                        for (int i = 0; i < RandomUtil.getRandom(1, 4); i++) {
                            methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.SWAP));
                        }
                    }
                });
    }

    private void popSwapIntegers(final MethodNode methodNode) {
        Arrays.stream(methodNode.instructions.toArray()).forEach(insnNode -> {
            if (insnNode.getOpcode() >= Opcodes.ICONST_M1 && insnNode.getOpcode() <= Opcodes.ICONST_5) {
                methodNode.instructions.insertBefore(insnNode, new InsnNode(insnNode.getOpcode()));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.POP));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.SWAP));
            } else if (insnNode instanceof IntInsnNode && insnNode.getOpcode() != Opcodes.NEWARRAY) {
                methodNode.instructions.insertBefore(insnNode,
                        new IntInsnNode(insnNode.getOpcode(), ((IntInsnNode) insnNode).operand));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.POP));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.SWAP));
            } else if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst instanceof Integer) {
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(((LdcInsnNode) insnNode).cst));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.POP));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.SWAP));
            }
        });
    }

    private void massiveSignature(final ClassNode classNode) {
        final String signature = StringUtil.getMassiveString();
        classNode.fields.forEach(fieldNode -> fieldNode.signature = signature);
        classNode.methods.forEach(methodNode -> {
            methodNode.signature = signature;
            if (methodNode.localVariables != null) {
                methodNode.localVariables.forEach(variableNode -> variableNode.signature = signature);
            }
        });
        classNode.signature = signature;
    }

    private void massiveSource(final ClassNode classNode) {
        classNode.sourceFile = StringUtil.getMassiveString();
        classNode.sourceDebug = StringUtil.getMassiveString();
    }

    private void pushVarargs(final ClassNode classNode) {
        if (!Modifier.isInterface(classNode.access)) {
            classNode.methods.stream().filter(methodNode -> (methodNode.access & Opcodes.ACC_SYNTHETIC) == 0
                    && (methodNode.access & Opcodes.ACC_BRIDGE) == 0).forEach(methodNode -> methodNode.access |= Opcodes.ACC_VARARGS);
        }
    }

    private void pushTransient(final ClassNode classNode) {
        if (!Modifier.isInterface(classNode.access)) {
            classNode.fields.forEach(fieldNode -> fieldNode.access |= Opcodes.ACC_TRANSIENT);
        }
    }

    private void invalidAnnotations(final ClassNode classNode) {
        if (classNode.invisibleAnnotations == null) {
            classNode.invisibleAnnotations = new ArrayList<>();
        }


        classNode.invisibleAnnotations.add(getAnnotationNode());

        classNode.fields
                .forEach(f -> {
                    if (f.invisibleAnnotations == null)
                        f.invisibleAnnotations = new ArrayList<>();

                    f.invisibleAnnotations.add(getAnnotationNode());
                });

        classNode.methods
                .forEach(m -> {
                    if (m.invisibleAnnotations == null)
                        m.invisibleAnnotations = new ArrayList<>();
                    m.invisibleAnnotations.add(getAnnotationNode());
                });
    }

    private AnnotationNode getAnnotationNode() {
        return new AnnotationNode("L" + EMPTY_STRINGS + ";");
    }


    private void randomDescriptor(final ClassNode classNode) {
        classNode.methods.stream().filter(ASMUtil::hasLabels).forEach(methodNode -> {
            final Set<Integer> set = new HashSet<>();
            methodNode.localVariables = new ArrayList<>();
            Arrays.stream(methodNode.instructions.toArray()).filter(insnNode -> insnNode instanceof VarInsnNode)
                    .forEach(insnNode -> set.add(((VarInsnNode) insnNode).var));
            set.forEach(value -> methodNode.localVariables.add(new LocalVariableNode("",
                    this.getConfiguration().getMiscellaneousObfuscation().getVariableDescriptorList()
                            .get(RandomUtil.getRandom(this.getConfiguration().getMiscellaneousObfuscation()
                                    .getVariableDescriptorList().size())),
                    null, ASMUtil.getFirstLabel(methodNode), ASMUtil.getLastLabel(methodNode), value)));
        });
    }

}

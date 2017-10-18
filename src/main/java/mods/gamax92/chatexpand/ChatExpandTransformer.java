package mods.gamax92.chatexpand;

import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.IClassTransformer;

public class ChatExpandTransformer implements IClassTransformer {
	private byte[] transformPatchPacket(String name, String transformedName, byte[] bytes) {
		ChatExpandCore.logger.log(Level.INFO, "Patching class: " + transformedName);

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode(Opcodes.ASM4);
		cr.accept(cn, 0);

		boolean patched1 = false;
		boolean patched2 = false;

		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("<init>") && mn.desc.equals("(Ljava/lang/String;)V")) {
				ChatExpandCore.logger.log(Level.INFO, "Patching method: " + mn.name);
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode instanceof IntInsnNode && currentNode.getOpcode() == Opcodes.BIPUSH) {
						IntInsnNode killnode = (IntInsnNode) currentNode;
						if (killnode.operand == 100) {
							AbstractInsnNode replaceWith = new FieldInsnNode(Opcodes.GETSTATIC, "mods/gamax92/chatexpand/ChatExpandCore", "CHAT_LENGTH", "I");
							mn.instructions.set(killnode, replaceWith);
							patched1 = true;
						}
					}
				}
			} else if (mn.name.equals("func_148837_a") && mn.desc.equals("(Lnet/minecraft/network/PacketBuffer;)V")) {
				ChatExpandCore.logger.log(Level.INFO, "Patching method: " + mn.name);
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode instanceof IntInsnNode && currentNode.getOpcode() == Opcodes.BIPUSH) {
						IntInsnNode killnode = (IntInsnNode) currentNode;
						if (killnode.operand == 100) {
							AbstractInsnNode replaceWith = new FieldInsnNode(Opcodes.GETSTATIC, "mods/gamax92/chatexpand/ChatExpandCore", "CHAT_LENGTH", "I");
							mn.instructions.set(killnode, replaceWith);
							patched2 = true;
						}
					}
				}
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		if (!patched1 && !patched2) {
			ChatExpandCore.logger.log(Level.ERROR, "Failed to patch method, constructor and readPacketData not patched.");
		} else if (!patched1) {
			ChatExpandCore.logger.log(Level.ERROR, "Failed to patch method, constructor not patched.");
		} else if (!patched2) {
			ChatExpandCore.logger.log(Level.ERROR, "Failed to patch method, readPacketData not patched.");
		} else {
			ChatExpandCore.logger.log(Level.INFO, "Method was successfully patched!");
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private byte[] transformPatchGui(String name, String transformedName, byte[] bytes) {
		ChatExpandCore.logger.log(Level.INFO, "Patching class: " + transformedName);

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode(Opcodes.ASM4);
		cr.accept(cn, 0);

		boolean patched = false;

		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("func_73866_w_") && mn.desc.equals("()V")) {
				ChatExpandCore.logger.log(Level.INFO, "Patching method: " + mn.name);
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode currentNode = iter.next();

					if (currentNode instanceof IntInsnNode && currentNode.getOpcode() == Opcodes.BIPUSH) {
						IntInsnNode killnode = (IntInsnNode) currentNode;
						if (killnode.operand == 100) {
							AbstractInsnNode replaceWith = new FieldInsnNode(Opcodes.GETSTATIC, "mods/gamax92/chatexpand/ChatExpandCore", "CHAT_LENGTH", "I");
							mn.instructions.set(killnode, replaceWith);
							patched = true;
						}
					}
				}
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		if (patched) {
			ChatExpandCore.logger.log(Level.INFO, "Method was successfully patched!");
			bytes = cw.toByteArray();
		} else {
			ChatExpandCore.logger.log(Level.ERROR, "Failed to patch method.");
		}

		return bytes;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes != null) {
			if (transformedName.equals("net.minecraft.network.play.client.CPacketChatMessage"))
				return transformPatchPacket(name, transformedName, bytes);
			else if (transformedName.equals("net.minecraft.client.gui.GuiChat"))
				return transformPatchGui(name, transformedName, bytes);
		}
		return bytes;
	}
}
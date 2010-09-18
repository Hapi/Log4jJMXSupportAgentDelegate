package com.hapiware.asm.log4j;

import java.util.List;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

import com.google.inject.cglib.asm.Opcodes;
import com.hapiware.asm.log4j.Log4jJmxSupportTransformer.LoggerDesc;



public class Log4jJmxSupportAdapter extends MethodAdapter
{
	private final List<LoggerDesc> _loggerDescs;
	private final String _owner;

	public Log4jJmxSupportAdapter(String owner, List<LoggerDesc> loggerDescs, MethodVisitor mv)
	{
		super(mv);
		_owner = owner;
		_loggerDescs = loggerDescs;
	}
	
	@Override
	public void visitInsn(int opcode) {
		if((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
			for(LoggerDesc ld : _loggerDescs) {
				mv.visitFieldInsn(Opcodes.GETSTATIC, _owner, ld.getName(), ld.getDesc());
				mv.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"com/hapiware/jmx/log4j/JmxLog4jLogger",
					"register",
					"(Lorg/apache/log4j/Logger;)V"
				);
			}
		}
		mv.visitInsn(opcode);
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals)
	{
		mv.visitMaxs(maxStack + 2, maxLocals);
	}
}

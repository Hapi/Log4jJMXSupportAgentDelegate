package com.hapiware.asm.log4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_STATIC;


public class Log4jJmxSupportTransformer
	implements
		ClassFileTransformer
{
	private Pattern[] _includePatterns;
	private Pattern[] _excludePatterns;
	private Pattern[] _loggerPatterns;
	
	public Log4jJmxSupportTransformer(Pattern[] includePatterns, Pattern[] excludePatterns)
	{
		_includePatterns = includePatterns;
		_excludePatterns = excludePatterns;
		_loggerPatterns = new Pattern[] { Pattern.compile("Lorg/apache/log4j/Logger;") };
	}

	public byte[] transform(
		ClassLoader loader,
		final String className,
		Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain,
		byte[] classFileBuffer) throws IllegalClassFormatException
	{
		for(Pattern p : _excludePatterns)
			if(p.matcher(className).matches())
				return null;
		
		for(Pattern p : _includePatterns) {
			if(p.matcher(className).matches()) 
			{
				try
				{
					final List<LoggerDesc> loggerDescs = new ArrayList<LoggerDesc>();
					ClassReader cr = new ClassReader(classFileBuffer);
					ClassWriter cw = new ClassWriter(0);
					cr.accept(
						new ClassAdapter(cw)
						{
							public FieldVisitor visitField(
								int access,
								String name,
								String desc,
								String signature,
								Object value
							)
							{
								FieldVisitor fv =
									super.visitField(access, name, desc, signature, value);
								for(Pattern p : _loggerPatterns) {
									if((access & ACC_STATIC) == ACC_STATIC && p.matcher(desc).matches())
										loggerDescs.add(new LoggerDesc(name, desc));
								}
								return fv;
							}
							
							public MethodVisitor visitMethod(
								int access,
								String name,
								String desc,
								String signature,
								String[] exceptions
							)
							{
								MethodVisitor mv =
									super.visitMethod(access, name, desc, signature, exceptions);
								if(name.equals("<clinit>"))
									return new Log4jJmxSupportAdapter(className, loggerDescs, mv);
								else
									return mv;
							} 
						},
						0
					);
					return cw.toByteArray();
				}
				catch(Throwable e)
				{
					throw new Error("Instrumentation of a class " + className + " failed.", e);
				}
			}
		}
		return null;
	}
	
	public static class LoggerDesc
	{
		private final String _name;
		private final String _desc;
		
		public LoggerDesc(String name, String desc)
		{
			_name = name;
			_desc = desc;
		}

		public String getName()
		{
			return _name;
		}

		public String getDesc()
		{
			return _desc;
		}
	}
}

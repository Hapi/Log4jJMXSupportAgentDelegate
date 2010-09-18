package com.hapiware.asm.log4j;

import java.lang.instrument.Instrumentation;
import java.util.regex.Pattern;

/**
 * Adds a JMX support for log4j in the case where the source code is not available or if the source
 * code does not wanted to be changed for some other reason. {@code Log4jJmxSupportAgentDelegate}
 * adds the JMX support during the class loading. See {@link com.hapiware.asm.agent.Agent}
 * documentation to see how to filter classes to be instrumented. 
 * 
 * 
 * <h1>Notice</h1>
 * Only static {@code org.apache.log4j.Logger} objects will have the JMX support. This should
 * be sufficient but log4j loggers have been used quite creatively then the source code must
 * be refined (maybe using {@link com.hapiware.jmx.log4j.JmxLog4jLogger} directly thus eliminating
 * the need for {@code Log4jJmxSupportAgentDelegate} altogether.
 * 
 * 
 * <h1>Remember classpath</h1>
 * Remember to add a suitable version of {@code log4jJmxSupport-x.x.x.jar} to the classpath
 * of the target JVM. A Maven groupId for {@code log4jJmxSupport-x.x.x.jar} is
 * {@code com.hapiware.jmx.log4j}. 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 * @see com.hapiware.asm.agent.Agent
 * @see com.hapiware.jmx.log4j.JmxLog4jLogger
 */
public class Log4jJmxSupportAgentDelegate
{
	public static void premain(
		Pattern[] includePatterns,
		Pattern[] excludePatterns,
		Object config,
		Instrumentation instrumentation
	)
	{
		try {
			instrumentation.addTransformer(
				new Log4jJmxSupportTransformer(includePatterns, excludePatterns)
			);
		}
		catch(Exception e) 
		{
			System.err.println(
				"Couldn't start the Log4jJmxSupport agent delegate due to an exception. "
					+ e.getMessage()
			);
			e.printStackTrace();
		}
	}
}

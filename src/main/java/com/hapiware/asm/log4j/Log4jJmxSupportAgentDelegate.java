package com.hapiware.asm.log4j;

import java.lang.instrument.Instrumentation;
import java.util.regex.Pattern;


/**
 * Adds a JMX support for log4j in the case where the source code is not available or if the source
 * code does not wanted to be changed for some other reason. {@code Log4jJmxSupportAgentDelegate}
 * adds the JMX support during the class loading.
 *  
 * 
 * <h3>com.hapiware.agent.Agent required</h3>
 * {@code Log4jJmxSupportAgentDelegate} requires {@code com.hapiware.agent.Agent}. For more
 * information see {@code com.hapiware.agent.Agent}.
 * 
 * 
 * <h3>Notice</h3>
 * Only static {@code org.apache.log4j.Logger} objects will have the JMX support. This should
 * be sufficient but if log4j loggers have been used too creatively then the source code must
 * be refined (maybe using {@link com.hapiware.jmx.log4j.JmxLog4jLogger} directly thus eliminating
 * the need for {@code Log4jJmxSupportAgentDelegate} altogether).
 * 
 * 
 * <h3>Remember classpath</h3>
 * Remember to add a suitable version of {@code log4jJmxSupport-x.x.x.jar} to the classpath
 * of the target JVM. This jar file is needed to add the JMX supprt for log4j. A Maven groupId
 * for {@code log4jJmxSupport-x.x.x.jar} is {@code com.hapiware.jmx.log4j}. 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 * @see com.hapiware.asm.agent.Agent
 * @see com.hapiware.jmx.log4j.JmxLog4jLogger
 */
public class Log4jJmxSupportAgentDelegate
{
	/**
	 * This method is called by the general agent {@code com.hapiware.agent.Agent} and
	 * is done before the main method call right after the JVM initialisation. 
	 * <p>
	 * <b>Notice</b> the difference between this method and 
	 * the {@code public static void premain(String, Instrumentation} method described in
	 * {@code java.lang.instrument} package. 
	 *
	 * @param includePatterns
	 * 		A list of patterns to include classes for instrumentation.
	 * 
	 * @param excludePatterns
	 * 		A list patterns to set classes not to be instrumented.
	 * 
	 * @param config
	 * 		Not used.
	 * 
	 * @param instrumentation
	 * 		See {@link java.lang.instrument.Instrumentation}
	 * 
	 * @throws IllegalArgumentException
	 * 		If there is something wrong with the configuration file.
	 *
	 * @see java.lang.instrument
	 */
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

package com.cvnavi.browser;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
 
/**
 * 浏览器在新的jvm中运行，需要它自己的日志。
 * @author lixy
 *
 */
public class CustomConfigurationFactory extends ConfigurationFactory {
	static {
		setCatalinaHome();
	}

	static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
		builder.setConfigurationName(name);
		builder.setStatusLevel(Level.ERROR);

		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern",
				"%d{MM-dd HH:mm:ss} %-5level %logger{1} - %L - %msg%n");

		// create the console appender
		AppenderComponentBuilder consoleBuilder = builder.newAppender("Stdout", ConsoleAppender.PLUGIN_NAME)
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		consoleBuilder.add(layoutBuilder);
		builder.add(consoleBuilder);

		// create the rolling file appender
		ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
				.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));

		AppenderComponentBuilder rollingFileBuilder = builder
				.newAppender("RollingFile", RollingFileAppender.PLUGIN_NAME)
				.addAttribute("fileName", "${sys:catalina.home}/logs/browser.log")
				.addAttribute("filePattern", "${sys:catalina.home}/logs/browser-%d{yyyy-MM-dd}.log.gz")
				.add(layoutBuilder).addComponent(triggeringPolicy);
		builder.add(rollingFileBuilder);

		builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("RollingFile")));

		return builder.build();
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final String name,
			final URI configLocation) {
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
		return createConfiguration(name, builder);
	}

	@Override
	protected String[] getSupportedTypes() {
		return new String[] { "*" };
	}

	static void setCatalinaHome() {
		String key = "catalina.home";
		String catalinaHome = null;//"C:\\Develop\\apache-tomcat-8.5.9";
		if (System.getProperty(key) == null) {
			catalinaHome = System.getenv("catalina.home");
			if (catalinaHome == null) { 
				File current = new File(
						CustomConfigurationFactory.class.getProtectionDomain().getCodeSource().getLocation().getFile());
				File home=current.getParentFile().getParentFile().getParentFile().getParentFile();
				String logs=home+File.separator+"logs";
				if(Files.exists(Paths.get(logs))){
					catalinaHome=home.getAbsolutePath();
				}else{
					catalinaHome=current.getAbsolutePath();
				}
			}
			System.setProperty(key, catalinaHome);
		}
	}
}
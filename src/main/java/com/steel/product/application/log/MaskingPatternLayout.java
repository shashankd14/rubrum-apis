package com.steel.product.application.log;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Logback appender to mask a given pattern with a mask value. Both pattern and
 * mask value can be configured in the logback.xml E.g. Sensitive data like
 * emails will be masked to * in the logs. Logback layout:
 * <layout class="main.utils.services.utils.MaskingPatternLayout">
 * <mask>*</mask>
 * <patternsProperty>^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$</patternsProperty>
 * <pattern>%d [%thread] %-5level %logger{35} - %msg%n</pattern> </layout>
 */
public class MaskingPatternLayout extends PatternLayout {

	/**
	 * Value with which the pattern is to be masked with
	 */
	public String mask;

	/**
	 * Regex pattern to be checked in the logs for masking
	 */
	public String patternsProperty;

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getPatternsProperty() {
		return patternsProperty;
	}

	public void setPatternsProperty(String patternsProperty) {
		this.patternsProperty = patternsProperty;
	}

	/**
	 * Performing masking if the regex pattern matches a value in the logs. Each log
	 * message will be split and checked for pattern matching
	 *
	 * @param event log event
	 * @return a masked message if the regex pattern matches.
	 */
	@Override
	public String doLayout(ILoggingEvent event) {
		String defaultParsedEvent;
		if (!isStarted()) {
			defaultParsedEvent = CoreConstants.EMPTY_STRING;
		}
		defaultParsedEvent = writeLoopOnConverters(event);
		String test = (!StringUtils.isBlank(patternsProperty) && !StringUtils.isBlank(mask))
		? Stream.of(defaultParsedEvent.split("\\s+")).map(this::maskMessage).collect(Collectors.joining(" "))
				.concat(CoreConstants.LINE_SEPARATOR)
		: event.getMessage().concat(CoreConstants.LINE_SEPARATOR);
				System.out.println("Test : " + test);
//		return (!StringUtils.isBlank(patternsProperty) && !StringUtils.isBlank(mask))
//				? Stream.of(defaultParsedEvent.split("\\s+")).map(this::maskMessage).collect(Collectors.joining(" "))
//						.concat(CoreConstants.LINE_SEPARATOR)
//				: event.getMessage().concat(CoreConstants.LINE_SEPARATOR);
			return test;
	}

	/**
	 * Mask the word if the pattern matches else return the word as is
	 *
	 * @param event a single word from the log message
	 * @return Masked word if the pattern matches else return the word as is
	 */
	private String maskMessage(String event) {
		Pattern pattern = Pattern.compile(patternsProperty);
		Matcher matcher = pattern.matcher(event);
		System.out.println("Matches :: " + matcher.matches());
		return (matcher.matches())
				? IntStream.range(0, event.length()).mapToObj(count -> mask).collect(Collectors.joining())
				: event;
	}
}
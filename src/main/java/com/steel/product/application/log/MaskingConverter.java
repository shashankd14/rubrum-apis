package com.steel.product.application.log;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class MaskingConverter<E extends ILoggingEvent> extends CompositeConverter<E> {

	private String patternsProperty;
	Pattern p;

	public String getPatternsProperty() {
		return patternsProperty;
	}

	public void setPatternsProperty(String patternsProperty) {
		this.patternsProperty = patternsProperty;
	}

	@Override
	protected String transform(E event, String in) {
		if (!started) {
			return in;
		}
		String pattern = MDC.get("pattern");
		String replacement = MDC.get("replacement");
		if (pattern != null && replacement != null) {
			StringTokenizer st = new StringTokenizer(pattern, "~");
			while (st.hasMoreTokens()) {
				String subPattern = st.nextToken();
				p = Pattern.compile(subPattern, Pattern.CASE_INSENSITIVE);
				Matcher pMatcher = p.matcher(in);
				if (pMatcher.find()) {
					in = pMatcher.replaceAll(replacement);
				}
			}
			MDC.put("transformedMessage", in);
		}
		return in;
	}
}
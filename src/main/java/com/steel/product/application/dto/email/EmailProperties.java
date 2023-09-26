package com.steel.product.application.dto.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "email")
@Configuration
@Getter
@Setter
public class EmailProperties {

	private String subject;
	private String text;
	private Boolean isHTML;
	private Boolean sendReportEmail;
	private String reportScheduleTemplateName;
}

package com.steel.product.application.audit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.AuditReqRespTrackerRepository;
import com.steel.product.application.entity.AuditReqRespTrackerEntity;

import lombok.extern.log4j.Log4j2;

//@Aspect
@Component
@Log4j2
public class AuditLoggingAspect {

	@Autowired
	AuditReqRespTrackerRepository auditRepo;

    private final ObjectMapper mapper = new ObjectMapper();

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Around("controller() || restController()")
    public Object log(final ProceedingJoinPoint pjp) throws Throwable {
    	    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
    	            .getRequest();
    	    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
    	            .getResponse();

    	    String url = request.getRequestURL().toString();
    	    String method = request.getMethod();
    	    String contentType = request.getContentType();
    	    String requestType = contentType != null ? contentType.split(";")[0] : "";

    	    AuditReqRespTrackerEntity logEntity = new AuditReqRespTrackerEntity();
    	    logEntity.setReqTime(LocalDateTime.now());

    	    Object value = null;

    	    try {
    	        value = pjp.proceed();
    	        logEntity.setRespTime(LocalDateTime.now());
    	        logEntity.setResponseStatus(response.getStatus() == HttpStatus.OK.value() ? "SUCCESS" : "FAILED");
    	    } catch (Throwable throwable) {
    	        value = handleException(throwable, logEntity);
    	        logEntity.setRespTime(LocalDateTime.now());
    	        logEntity.setResponseStatus("FAILED");

    	     // Log request data based on method
	            String requestData = getRequestData(request, pjp.getArgs(), method, requestType);
	            logEntity.setReqObject(requestData);
	            populateFromJson(requestData, url, logEntity, request);

	            if (value != null && !"/error".equals(request.getRequestURI())) {
	                logEntity.setRespObject(mapper.writeValueAsString(value));
	            }
	            if (!url.contains("/auditlog/list") && !url.contains("/auditlog/getAdminAuditLogs")) {
	                try {
	            	    logEntity.setId(null);
						auditRepo.save(logEntity);
					} catch (Exception e) {
						log.error("Failed to save in audit request/response table == ", e.getMessage());
					}
				}
    	        throw throwable;
    	    } finally {
    	        if (isSwaggerCall(request.getRequestURI())) return value;

    	        try {
    	            // Log request data based on method
    	            String requestData = getRequestData(request, pjp.getArgs(), method, requestType);
    	            logEntity.setReqObject(requestData);
    	            populateFromJson(requestData, url, logEntity, request);

    	            if (value != null && !"/error".equals(request.getRequestURI())) {
    	                try {
							logEntity.setRespObject(mapper.writeValueAsString(value));
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
    	            }
    	            if (!url.contains("/auditlog/list") && !url.contains("/auditlog/getAdminAuditLogs")) {
    	                try {
							auditRepo.save(logEntity);
						} catch (Exception e) {
							log.error("Failed to save in audit request/response table == ", e.getMessage());
						}
					}
    	        } catch (Exception e) {
    	            log.error("Failed to audit request/response body", e);
    	        }
    	    }
    	    return value;
    }

    	private String getRequestData(HttpServletRequest request, Object[] args, String method, String requestType) {
    	    try {
    	        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
    	            return new JSONObject(request.getParameterMap()).toString();
    	        }

    	        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
    	            if (MediaType.MULTIPART_FORM_DATA_VALUE.equals(requestType)) {
    	                return getMultipartRequestData(request);
    	            }
    	            for (Object arg : args) {
    	                if (arg != null && !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
    	                    return mapper.writeValueAsString(arg);
    	                }
    	            }
    	        }
    	    } catch (Exception e) {
    	        log.warn("Failed to extract request data", e);
    	    }
    	    return "{}";
    	}

    	/**
    	 * Parses a multipart/form-data request into a flat JSON object: regular
    	 * form fields are stored as their string value, and file parts are stored
    	 * as an object with fileName/contentType/size (the file's binary content
    	 * itself is not captured).
    	 */
    	private String getMultipartRequestData(HttpServletRequest request) {
    	    JSONObject json = new JSONObject();
    	    try {
    	        for (Part part : request.getParts()) {
    	            String name = part.getName();
    	            String submittedFileName = part.getSubmittedFileName();
    	            if (submittedFileName != null) {
    	                JSONObject fileInfo = new JSONObject();
    	                fileInfo.put("fileName", submittedFileName);
    	                fileInfo.put("contentType", part.getContentType());
    	                fileInfo.put("size", part.getSize());
    	                json.put(name, fileInfo);
    	            } else {
    	                json.put(name, readPartAsString(part));
    	            }
    	        }
    	    } catch (Exception e) {
    	        log.warn("Failed to parse multipart request data", e);
    	    }
    	    return json.toString();
    	}

    	private String readPartAsString(Part part) throws Exception {
    	    try (InputStream is = part.getInputStream();
    	            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    	        byte[] buffer = new byte[4096];
    	        int len;
    	        while ((len = is.read(buffer)) != -1) {
    	            baos.write(buffer, 0, len);
    	        }
    	        return baos.toString(StandardCharsets.UTF_8.name());
    	    }
    	}

    private boolean isSwaggerCall(String uri) {
        return uri.contains("swagger") || uri.contains("/v3/api-docs");
    }

    private Object handleException(Throwable throwable, AuditReqRespTrackerEntity logEntity) {
        Object value = throwable;
        log.error("Error during method execution", value);
        logEntity.setRespObject(value.toString());
        return value;
    }

	private void populateFromJson(String jsonString, String requestUrl, AuditReqRespTrackerEntity logEntity,
			HttpServletRequest request) {
		JSONObject json = new JSONObject(jsonString);
		String userName = json.optString("userName", "");
		String requestId = json.optString("requestId", json.optString("requestType", ""));
		String coilNumber = json.optString("coilNumber", "");
		String ipAddress = json.has("ipAddress") ? json.getString("ipAddress") : request.getRemoteAddr();

		if (userName != null && userName.length() > 0) {
			logEntity.setUserName(userName);
		}
		logEntity.setRequestId(requestId);
		logEntity.setRequestUrl(requestUrl);
		logEntity.setCoilNumber(coilNumber);
		logEntity.setIpAddress(ipAddress);
	}
}
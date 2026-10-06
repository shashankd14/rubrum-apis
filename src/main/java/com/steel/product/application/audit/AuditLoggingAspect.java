package com.steel.product.application.audit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.dao.AuditReqRespTrackerRepository;
import com.steel.product.application.entity.AuditReqRespTrackerEntity;

import lombok.extern.log4j.Log4j2;

//@Aspect
@Component
@Log4j2
public class AuditLoggingAspect {

	/** Keys (lower-cased, underscores removed) that carry an inward entry id. */
	private static final Set<String> INWARD_ID_KEYS = new HashSet<>(
			Arrays.asList("inwardid", "inwardids", "inwardentryid", "inwardentryids"));

	/** Keys (lower-cased, underscores removed) that carry a coil number directly. */
	private static final Set<String> COIL_NUMBER_KEYS = new HashSet<>(
			Arrays.asList("coilnumber", "coilnumbers", "coilno"));

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
			logEntity.setResponseStatus(
					response != null && response.getStatus() == HttpStatus.OK.value() ? "SUCCESS" : "FAILED");
			return value;
		} catch (Throwable throwable) {
			handleException(throwable, logEntity);
			logEntity.setRespTime(LocalDateTime.now());
			logEntity.setResponseStatus("FAILED");
			throw throwable;
		} finally {
			// Single save point for both success and failure (previously failures were saved twice).
			if (!isSwaggerCall(request.getRequestURI())) {
				try {
					String requestData = getRequestData(request, pjp.getArgs(), method, requestType);
					logEntity.setReqObject(requestData);
					populateFromJson(requestData, url, logEntity, request);

					if (value != null && !"/error".equals(request.getRequestURI())) {
						try {
							logEntity.setRespObject(mapper.writeValueAsString(value));
						} catch (Throwable e) { // incl. StackOverflowError from bidirectional entities
							log.warn("Failed to serialize response for audit: {}", e.toString());
						}
					}
					if (!url.contains("/auditlog/list") && !url.contains("/auditlog/getAdminAuditLogs")) {
						try {
							auditRepo.save(logEntity);
						} catch (Exception e) {
							log.error("Failed to save in audit request/response table == {}", e.getMessage());
						}
					}
				} catch (Throwable e) { // auditing must never break the API response
					log.error("Failed to audit request/response body", e);
				}
			}
		}
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

	private void handleException(Throwable throwable, AuditReqRespTrackerEntity logEntity) {
		log.error("Error during method execution", throwable);
		logEntity.setRespObject(throwable.toString());
	}

	// ------------------------------------------------------------------
	// Request field extraction (works for objects, arrays, nested JSON,
	// GET param maps and multipart fields holding JSON strings)
	// ------------------------------------------------------------------

	private void populateFromJson(String jsonString, String requestUrl, AuditReqRespTrackerEntity logEntity,
			HttpServletRequest request) {
		JsonNode root = parse(jsonString);

		String userName = findFirst(root, "username");
		String requestId = firstNonEmpty(findFirst(root, "requestid"), findFirst(root, "requesttype"));
		String ipAddress = firstNonEmpty(findFirst(root, "ipaddress"), request.getRemoteAddr());

		if (userName == null || userName.isEmpty()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
		}

		// coilNumber and/or inwardId from anywhere in the request
		Set<String> coilNumbers = new LinkedHashSet<>();
		collectValues(root, COIL_NUMBER_KEYS, coilNumbers);
		collectValues(root, INWARD_ID_KEYS, coilNumbers);

		logEntity.setUserName(userName);
		logEntity.setRequestId(requestId);
		logEntity.setRequestUrl(requestUrl);
		logEntity.setCoilInwardId( coilNumbers.isEmpty() ? null : String.join(",", coilNumbers));
		logEntity.setIpAddress(ipAddress);
	}

	private JsonNode parse(String json) {
		try {
			if (json != null && !json.trim().isEmpty()) {
				return mapper.readTree(json);
			}
		} catch (Exception e) {
			log.debug("Audit request body is not JSON: {}", e.getMessage());
		}
		return mapper.createObjectNode();
	}

	private static String normalizeKey(String key) {
		return key == null ? "" : key.replace("_", "").toLowerCase();
	}

	/** Recursively collects scalar values of any matching key, at any depth. */
	private void collectValues(JsonNode node, Set<String> keys, Set<String> out) {
		if (node == null || node.isNull()) {
			return;
		}
		if (node.isObject()) {
			node.fields().forEachRemaining(e -> {
				if (keys.contains(normalizeKey(e.getKey()))) {
					addScalars(e.getValue(), out);
				} else {
					collectValues(e.getValue(), keys, out);
				}
			});
		} else if (node.isArray()) {
			node.forEach(child -> collectValues(child, keys, out));
		} else if (node.isTextual()) {
			// e.g. multipart field containing a JSON string
			String text = node.asText().trim();
			if (text.startsWith("{") || text.startsWith("[")) {
				collectValues(parse(text), keys, out);
			}
		}
	}

	/** Adds a scalar or each scalar of an array (GET params arrive as ["1809"]). */
	private void addScalars(JsonNode value, Set<String> out) {
		if (value == null || value.isNull()) {
			return;
		}
		if (value.isArray()) {
			value.forEach(v -> addScalars(v, out));
		} else if (value.isValueNode()) {
			String s = value.asText().trim();
			if (!s.isEmpty()) {
				out.add(s);
			}
		}
	}

	private String findFirst(JsonNode root, String key) {
		Set<String> found = new LinkedHashSet<>();
		collectValues(root, Collections.singleton(key), found);
		return found.isEmpty() ? null : found.iterator().next();
	}

	private static String firstNonEmpty(String a, String b) {
		return (a != null && !a.isEmpty()) ? a : b;
	}
 
}
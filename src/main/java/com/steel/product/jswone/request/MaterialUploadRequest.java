package com.steel.product.jswone.request;

import org.springframework.web.multipart.MultipartFile;
import com.steel.product.trading.request.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MaterialUploadRequest extends BaseRequest {

	private MultipartFile file;
	private String remarks;

}

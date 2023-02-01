package com.steel.product.application.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CommonUtil {

	static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	public static String persistFiles(String applicationJarPath, String stageName, String templateName,
			MultipartFile file) throws IOException {
		String path = applicationJarPath + File.separator + stageName + File.separator+ templateName;
		String jsonFile = path + File.separator + file.getOriginalFilename();
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		InputStream inputStream = file.getInputStream();
		FileOutputStream outStream = new FileOutputStream(new File(jsonFile));
		byte[] contents = new byte[inputStream.available()];
		int length;
		while ((length = inputStream.read(contents)) > 0) {
			outStream.write(contents, 0, length);
		}
		inputStream.close();
		outStream.close();
		return jsonFile;
	}

}
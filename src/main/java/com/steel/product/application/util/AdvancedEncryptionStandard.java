/*******************************************************************************
 * Copyright (c) 2016 Techurate Systems Pvt Ltd.
 *
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system,
 * adopted or transmitted in any form or by any means, electronic, mechanical, photographic,
 * graphic, optic recording or otherwise, translated in any language or computer language, without
 * the prior written permission of Techurate Systems Pvt Ltd.
 * Techurate Systems Pvt Ltd 
 * 227, 5th Main Rd, Indira Nagar II Stage,
 * Hoysala Nagar, Indiranagar, Bengaluru, 
 * Karnataka 560038, India
 *******************************************************************************/
package com.steel.product.application.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AdvancedEncryptionStandard {
	private static final String ALGORITHM = "AES";

	private byte[] key;

	public AdvancedEncryptionStandard(byte[] key) {
		this.key = key;
	}

	/**
	 * Encrypts the given plain text.
	 *
	 * @param plainText The plain text to encrypt.
	 */
	public byte[] encrypt(byte[] plainText) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		return cipher.doFinal(plainText);
	}

	/**
	 * Decrypts the given byte array.
	 *
	 * @param cipherText The data to decrypt.
	 */

	public byte[] decrypt(byte[] cipherText) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		return cipher.doFinal(cipherText);
	}

	/**
	 * Test method
	 */

	public static void main(String[] args) throws Exception {

		String encodedCaptcha = "";
		String decodedCaptcha = "";
		try {

			byte[] encryptionKey = ApplicationConstants.AES_KEY.getBytes(StandardCharsets.UTF_8);
			byte[] plainText = "TEST66@".getBytes(StandardCharsets.UTF_8);
			AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(encryptionKey);
			byte[] cipherText = advancedEncryptionStandard.encrypt(plainText);
			encodedCaptcha = Base64.getEncoder().encodeToString(cipherText);

			byte[] rawEncryptedPassword = Base64.getDecoder().decode(encodedCaptcha);
			byte[] decryptedCipherText = advancedEncryptionStandard.decrypt(rawEncryptedPassword);
			decodedCaptcha = new String(decryptedCipherText);
		} catch (Exception e) {
		}
		System.out.println(encodedCaptcha);
		System.out.println(decodedCaptcha);
	}

}

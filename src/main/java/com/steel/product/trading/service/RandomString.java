package com.steel.product.trading.service;

import java.security.SecureRandom;

public class RandomString {

	public String nextString() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int digitIndex = secureRandom.nextInt(10); // Random index within the range of digits (0-9)
			sb.append(characters.charAt(26 + digitIndex)); // Append the digit to the StringBuilder
		}
		for (int i = 0; i < 6; i++) {
			int randomIndex = secureRandom.nextInt(characters.length() - 10); // Exclude digits
			sb.append(characters.charAt(randomIndex));
		}
		for (int i = 0; i < sb.length(); i++) {
			int randomIndex = secureRandom.nextInt(sb.length());
			char temp = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(randomIndex));
			sb.setCharAt(randomIndex, temp);
		}
		System.out.println("Random Alphanumeric Number: " + sb.toString());
		return sb.toString();
	}

}
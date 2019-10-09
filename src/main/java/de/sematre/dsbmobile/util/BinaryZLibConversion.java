package de.sematre.dsbmobile.util;

import java.io.IOException;

public class BinaryZLibConversion {

	public static String decrypt(String encrypted) throws IOException {
		String bin = Base64JS.atob(encrypted);

		int[] charNumberArray = new int[bin.length()];
		for (int i = 0; i < charNumberArray.length; i++) {
			charNumberArray[i] = bin.charAt(i);
		}

		byte[] byteArray = new byte[charNumberArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) charNumberArray[i];
		}

		return GZIP.inflate(byteArray);
	}

	public static String encrypt(String decrypted) throws IOException {
		byte[] byteArray = GZIP.deflate(decrypted);

		int[] charNumberArray = new int[byteArray.length];
		for (int i = 0; i < charNumberArray.length; i++) {
			charNumberArray[i] = byteArray[i] < 0 ? byteArray[i] + 256 : byteArray[i];
		}

		char[] charArray = new char[charNumberArray.length];
		for (int i = 0; i < charArray.length; i++) {
			charArray[i] = (char) charNumberArray[i];
		}

		String bin = new String(charArray);

		return Base64JS.btoa(bin);
	}

}

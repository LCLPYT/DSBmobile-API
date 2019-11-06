package de.sematre.dsbmobile.util;

import java.io.IOException;

public class BinaryZLibConversion {

	public static String decrypt(String encrypted) throws IOException {
		return new String(decrypt(encrypted.getBytes()));
	}

	public static String encrypt(String decrypted) throws IOException {
		return new String(encrypt(decrypted).getBytes());
	}
	
	public static byte[] decrypt(byte[] encrypted) throws IOException {
		return GZIP.gunzip(Base64.decode(encrypted, 0));
	}

	public static byte[] encrypt(byte[] decrypted) throws IOException {
		return Base64.encode(GZIP.gzip(decrypted), 0);
	}

}

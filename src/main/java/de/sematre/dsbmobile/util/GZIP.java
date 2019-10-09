package de.sematre.dsbmobile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {

	public static String inflate(byte[] byteArray) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
		GZIPInputStream gin = new GZIPInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int bytes_read;
		byte[] buffer = new byte[1024];
		
		while ((bytes_read = gin.read(buffer)) > 0) {
			out.write(buffer, 0, bytes_read);
		}

		byte[] inflated = out.toByteArray();

		gin.close();
		out.close();

		return new String(inflated);
	}
	
	public static byte[] deflate(String s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gout = new GZIPOutputStream(out);
		
		gout.write(s.getBytes());
		
		gout.close();

		return out.toByteArray();
	}
	
}

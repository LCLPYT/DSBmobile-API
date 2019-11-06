package de.sematre.dsbmobile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {

	public static byte[] gzip(byte[] byteArray) {
		try {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(byteArray.length);
			final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(byteArray);
			gzipOutputStream.close();
			byteArray = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] gunzip(final byte[] array) {
		try {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
			final GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream, 32);
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			final byte[] array2 = new byte[32];
			while (true) {
				final int read = gzipInputStream.read(array2);
				if (read == -1) {
					break;
				}
				byteArrayOutputStream.write(array2, 0, read);
			}
			gzipInputStream.close();
			byteArrayInputStream.close();
			final byte[] byteArray = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

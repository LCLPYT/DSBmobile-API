package de.sematre.dsbmobile.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.sematre.dsbmobile.util.BinaryZLibConversion;

public class WebHandler {

	public static String fetchData(String user, String password) {
		try {
			String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSSSS", Locale.US).format(new Date());
			
			JsonObject postObj = new JsonObject();
			postObj.addProperty("AppId", UUID.randomUUID().toString());
			postObj.addProperty("PushId", "");
			postObj.addProperty("UserId", user);
			postObj.addProperty("UserPw", password);
			postObj.addProperty("AppVersion", "2.5.9");
			postObj.addProperty("Device", "WAS-L03T"); //Model name for Huawei P10 lite TODO on android, use custom function
			postObj.addProperty("OsVersion", "28 9"); //TODO on android, use custom function
			postObj.addProperty("Language", Locale.getDefault().getLanguage());
			postObj.addProperty("Date", formatted);
			postObj.addProperty("LastUpdate", formatted);
			postObj.addProperty("BundleId", "de.heinekingmedia.dsbmobile");
			
			byte[] postData = postObj.toString().getBytes();
			
			final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://app.dsbcontrol.de/JsonHandler.ashx/GetData").openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);

			JsonObject outer = new JsonObject();
			JsonObject inner = new JsonObject();
			inner.addProperty("Data", new String(BinaryZLibConversion.encrypt(postData)));
			inner.addProperty("DataType", 1);
			outer.add("req", inner);

			final byte[] bytes = outer.toString().getBytes(Charset.forName("UTF-8"));
			httpURLConnection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
			final DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
			dataOutputStream.write(bytes);
			dataOutputStream.close();
			
			httpURLConnection.connect();

			String result = null;
			
			int i = httpURLConnection.getResponseCode();
			if(i == 200) result = read(httpURLConnection);
			
			httpURLConnection.disconnect();
			
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String read(HttpURLConnection httpURLConnection) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 2048);
        final StringBuilder sb = new StringBuilder();
        
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null) break;
            sb.append(line);
        }
        
		String response = sb.toString();
		JsonObject obj = new Gson().fromJson(response, JsonObject.class);
		String data = obj.get("d").getAsString();
		
		return new String(BinaryZLibConversion.decrypt(data.getBytes()), "UTF-8");
	}
	
	/* ANDROID ONLY
    public static String getBuild() {
        return Build.MODEL;
    }*/

	/* ANDROID ONLY
	public static String getOsVersion() {
	    return Build.VERSION.SDK_INT + " " + Build.VERSION.RELEASE;
	}*/
	
}

package de.sematre.dsbmobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.sematre.dsbmobile.util.BinaryZLibConversion;
import net.dongliu.requests.Requests;

public class WebHandler {

	public static String fetchData(String user, String password) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			String current_time = format.format(new Date());
			
			JsonObject innerData = new JsonObject();
			innerData.addProperty("UserId", user);
			innerData.addProperty("UserPw", password);
			innerData.addProperty("AppVersion", "2.5.9");
			innerData.addProperty("Language", "de");
			innerData.addProperty("OsVersion", "28 9");
			innerData.addProperty("AppId", UUID.randomUUID().toString());
			innerData.addProperty("Device", "SM-G935F");
			innerData.addProperty("BundleId", "de.heinekingmedia.dsbmobile");
			innerData.addProperty("Date", current_time);
			innerData.addProperty("LastUpdate", current_time);
			
			JsonObject request = new JsonObject();
			JsonObject req = new JsonObject();
			req.addProperty("Data", BinaryZLibConversion.encrypt(innerData.toString()));
			req.addProperty("DataType", 1);
			request.add("req", req);
			
			String timetable_data = Requests.post("https://app.dsbcontrol.de/JsonHandler.ashx/GetData").jsonBody(request).send().readToText();
			
			JsonObject response = new Gson().fromJson(timetable_data, JsonObject.class);
			return BinaryZLibConversion.decrypt(response.get("d").getAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

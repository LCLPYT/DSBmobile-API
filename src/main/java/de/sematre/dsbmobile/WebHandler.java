package de.sematre.dsbmobile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.sematre.dsbmobile.util.BinaryZLibConversion;

public class WebHandler {

	public static String fetchData(String user, String password, String methodUrl) {
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);

		try {
			Map<String, String> formData = getFormData();

			login(formData, user, password);
			
			//Map<String, String> config = loadConfig();
			
			return getResult(methodUrl);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Map<String, String> getFormData() throws IOException {
		URL url = new URL("https://www.dsbmobile.de/");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

		try (InputStream is = conn.getInputStream()) {
			String s = IOUtils.toString(is, "UTF-8");
			conn.disconnect();

			Document d = Jsoup.parse(s);

			Elements forms = d.select("form");
			if(forms.size() < 1) return null;

			Element form = forms.get(0);
			if(form == null) return null;

			Elements hiddenInput = form.select("input[type=\"hidden\"]");
			if(hiddenInput == null || hiddenInput.size() < 1) return null;

			Map<String, String> params = new HashMap<>();
			for(Element e : hiddenInput) 
				params.put(e.attr("name"), e.val());

			return params;
		}
	}

	private static void login(Map<String, String> formData, String user, String password) throws IOException {
		List<NameValue> params = getParams(formData, "__LASTFOCUS", "__VIEWSTATE", "__VIEWSTATEGENERATOR", "__EVENTTARGET", "__EVENTARGUMENT", "__EVENTVALIDATION");
		params.add(new NameValue("txtUser", user));
		params.add(new NameValue("txtPass", password));
		params.add(new NameValue("ctl03", "Anmelden"));

		String urlEncoded = getUrlEncodedFormData(params);

		URL url = new URL("https://www.dsbmobile.de/Login.aspx?ReturnUrl=%2f");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			out.writeBytes(urlEncoded);
			out.flush();
		}

		conn.getResponseCode();
		
		try (InputStream in = conn.getInputStream()) {
			String s = IOUtils.toString(in, "UTF-8");
			conn.disconnect();

			Document d = Jsoup.parse(s);
			if(d.title().equals("DSBmobile - Login")) throw new IllegalArgumentException("Wrong username or password");
		}

		conn.disconnect();
	}
	
	/*private static Map<String, String> loadConfig() throws IOException {
		URL url = new URL("https://www.dsbmobile.de/scripts/configuration.js");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		String response;
		try (InputStream is = conn.getInputStream()) {
			response = IOUtils.toString(is, "UTF-8");
			conn.disconnect();
		}
		
		Map<String, String> config = new HashMap<>(); 
		
		Pattern p = Pattern.compile("METHOD\\:\\w*\\'(.*)\\'");
		Matcher m = p.matcher(response);
		
		String method = null;
		while(m.find()) {
			if(method != null) throw new IllegalStateException("There are more than two matches for " + p.toString() + " in the config.");
			try {
				method = m.group(1);
			} catch (IndexOutOfBoundsException e) {
				IllegalStateException ex = new IllegalStateException("The config is malformed or this program uses outdated regex patterns.");
				ex.addSuppressed(e);
				throw ex;
			}
		}
		if(method == null) throw new IllegalStateException("There is no match for " + p.toString() + " in the config.");
		else config.put("method", method);
		
		return config;
	}*/

	private static String getResult(String methodUrl) throws IOException {
		/*String method = config.get("method");
		if(method == null) throw new IllegalStateException("The loaded config does not contain a value for key 'method'.");
		
		URL url = new URL("https://www.dsbmobile.de/" + method);*/
		URL url = new URL(methodUrl);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		completePost(conn);
		
		String postData = getPostData();

		try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			out.writeBytes(postData);
			out.flush();
		}

		try (InputStream is = conn.getInputStream()) {
			String s = IOUtils.toString(is, "UTF-8");
			conn.disconnect();

			JsonObject obj = new Gson().fromJson(s, JsonObject.class);
			String data = obj.get("d").getAsString();

			return BinaryZLibConversion.decrypt(data);
		}
	}

	private static String getPostData() throws IOException {
		JsonObject obj = new JsonObject();
		JsonObject req = new JsonObject();
		req.addProperty("Data", getData());
		req.addProperty("DataType", 1);
		obj.add("req", req);

		String s = obj.toString();
		return s;
	}

	private static String getData() throws IOException { //TODO use config values! could break in the future
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		JsonObject obj = new JsonObject();
		obj.addProperty("UserId", "");
		obj.addProperty("UserPw", "");
		obj.add("Abos", new JsonArray());
		obj.addProperty("AppVersion", "2.3");
		obj.addProperty("Language", "de");
		obj.addProperty("OsVersion", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36 OPR/63.0.3368.107");
		obj.addProperty("AppId", "");
		obj.addProperty("Device", "WebApp");
		obj.addProperty("PushId", "");
		obj.addProperty("BundleId", "de.heinekingmedia.inhouse.dsbmobile.web");
		obj.addProperty("Date", format.format(new Date()));
		obj.addProperty("LastUpdate", format.format(new Date()));

		String json = obj.toString();

		return BinaryZLibConversion.encrypt(json);
	}

	private static void completePost(HttpsURLConnection conn) throws IOException {
		conn.setRequestProperty("Host", "www.dsbmobile.de");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Sec-Fetch-Mode", "cors");
		conn.setRequestProperty("Origin", "https://www.dsbmobile.de");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36 OPR/63.0.3368.107");
		conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		conn.setRequestProperty("Bundle_ID", "de.heinekingmedia.inhouse.dsbmobile.web");
		conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
		conn.setRequestProperty("Referer", "https://www.dsbmobile.de/");
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		conn.setRequestProperty("Accept-Language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");
	}

	private static String getUrlEncodedFormData(List<NameValue> params) throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();

		for(NameValue pair : params) {
			String s = getUrlEncoded(pair.getName()) + "=" + getUrlEncoded(pair.getValue());
			if(builder.length() <= 0) builder.append(s);
			else builder.append("&" + s);
		}

		return builder.toString();
	}

	private static String getUrlEncoded(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, "UTF-8");
	}

	private static List<NameValue> getParams(Map<String, String> formData, String... fillOutData) {
		List<NameValue> params = new ArrayList<>();

		for(String s : formData.keySet()) params.add(new NameValue(s, formData.get(s)));
		for(String s : fillOutData) {
			boolean exists = false;

			for(NameValue pair : params) {
				if(!pair.getName().equals(s)) continue;

				exists = true;
				break;
			}

			if(!exists) params.add(new NameValue(s, ""));
		}

		return params;
	}

	private static class NameValue {

		private String name, value;

		public NameValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

	}

}

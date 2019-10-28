package de.sematre.dsbmobile;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DSBMobile implements Serializable, Cloneable {

	private static final long serialVersionUID = -5265820858352981519L;
	private static final Gson gson = new Gson();
	private ArrayList<TimeTable> timeTables = null;
	private String username, password, customMethodUrl;

	public DSBMobile(String username, String password, String customMethodUrl) {
		this.username = username;
		this.password = password;
		this.customMethodUrl = customMethodUrl;
		
		getTimeTables(true);
	}
	
	public String getCustomMethodUrl() {
		return customMethodUrl;
	}
	
	public void setCustomMethodUrl(String customMethodUrl) {
		this.customMethodUrl = customMethodUrl;
	}

	public ArrayList<TimeTable> getTimeTables() {
		if(timeTables == null) getTimeTables(true);
		return timeTables;
	}
	
	public ArrayList<TimeTable> getTimeTables(boolean update) throws IllegalArgumentException{
		if(!update && timeTables != null) return timeTables;

		if(customMethodUrl == null) throw new IllegalArgumentException("The methodUrl might not be null!");
		
		String s = WebHandler.fetchData(username, password, customMethodUrl);
		if(s == null) throw new IllegalArgumentException("Something went wrong with the requests.");
		
		ArrayList<TimeTable> tables = getTimeTables(s);
		this.timeTables = tables;
		return tables;
	}

	public static ArrayList<TimeTable> getTimeTables(String json) {
		ArrayList<TimeTable> tables = new ArrayList<>();

		JsonObject obj = gson.fromJson(json, JsonObject.class);
		int result = obj.get("Resultcode").getAsInt();
		if(result != 0) throw new IllegalArgumentException("Wrong username or password");
		
		JsonArray resultMenuItems = obj.get("ResultMenuItems").getAsJsonArray();
		JsonObject contents = resultMenuItems.get(0).getAsJsonObject();
		JsonArray childs = contents.get("Childs").getAsJsonArray();
		JsonObject first = childs.get(0).getAsJsonObject();
		JsonObject root = first.get("Root").getAsJsonObject();
		JsonArray rootChilds = root.get("Childs").getAsJsonArray();
		
		for(JsonElement elem : rootChilds) {
			if(!elem.isJsonObject()) continue;
			JsonObject day = elem.getAsJsonObject();
			
			String date = day.get("Date").getAsString();
			String id = day.get("Id").getAsString();
			String groupName = day.get("Title").getAsString();
			
			JsonArray dayChilds = day.get("Childs").getAsJsonArray();
			for(JsonElement dayElem : dayChilds) {
				if(!dayElem.isJsonObject()) continue;
				JsonObject dayPage = dayElem.getAsJsonObject();
				
				String title = dayPage.get("Title").getAsString();
				String url = dayPage.get("Detail").getAsString();
				
				tables.add(new TimeTable(date, groupName, id, title, url));
			}
		}
		
		return tables;
	}

	@Deprecated
	public ArrayList<News> getNews() {
		/*ArrayList<News> tables = new ArrayList<>();

		String json = getStringFromURL(URL_PREFIX + "/news/" + key);
		for (JsonElement jElement : gson.fromJson(json, JsonArray.class)) {
			tables.add(new News(jElement.getAsJsonObject()));
		}*/

		return new ArrayList<>();
	}
	
	/**
	 * Checks if the given credentials are valid.
	 * 
	 * @param user The username.
	 * @param password The password.
	 * @return True, if the credentials are valid.
	 */
	public static boolean login(String user, String password) {
		try {
			WebHandler.fetchData(user, password, null);
			return true;
		} catch (IllegalArgumentException e) {
			return e.getMessage().equals("Wrong username or password");
		} catch (Exception e) {
			return true;
		}
	}

	public static class TimeTable implements Serializable, Cloneable {

		private static final long serialVersionUID = 553852884423090700L;
		private String date = "";
		private String groupName = "";
		private String id = "";
		private String title = "";
		private String url = "";

		public TimeTable(String date, String groupName, String id, String title, String url) {
			this.date = date;
			this.groupName = groupName;
			this.id = id;
			this.title = title;
			this.url = url;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
		
		public String getGroupName() {
			return groupName;
		}
		
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TimeTable other = (TimeTable) obj;
			if (date == null) {
				if (other.date != null) return false;
			} else if (!date.equals(other.date)) return false;
			if (id == null) {
				if (other.id != null) return false;
			} else if (!id.equals(other.id)) return false;
			if (groupName == null) {
				if (other.groupName != null) return false;
			} else if (!groupName.equals(other.groupName)) return false;
			if (title == null) {
				if (other.title != null) return false;
			} else if (!title.equals(other.title)) return false;
			if (url == null) {
				if (other.url != null) return false;
			} else if (!url.equals(other.url)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "{\"date\":\"" + date + "\", \"groupName\":\"" + groupName + "\", \"id\":\"" + id + "\", \"title\":\"" + title + "\", \"url\":\"" + url + "\"}";
		}
	}

	/*
	 * Not yet implemented with the new API
	 */
	@Deprecated
	public class News implements Serializable, Cloneable {

		private static final long serialVersionUID = 2336407351548626614L;
		private String headLine = "";
		private String date = "";
		private String id = "";
		private String imageUrl = "";
		private String shortMessage = "";
		private String wholeMessage = "";

		public News(String headLine, String date, String id, String imageUrl, String shortMessage, String wholeMessage) {
			this.headLine = headLine;
			this.date = date;
			this.id = id;
			this.imageUrl = imageUrl;
			this.shortMessage = shortMessage;
			this.wholeMessage = wholeMessage;
		}

		public News(JsonObject jsonObject) {
			this.headLine = jsonObject.get("headline").getAsString();
			this.date = jsonObject.get("newsdate").getAsString();
			this.id = jsonObject.get("newsid").getAsString();
			this.imageUrl = jsonObject.get("newsimageurl").getAsString();
			this.shortMessage = jsonObject.get("shortmessage").getAsString();
			this.wholeMessage = jsonObject.get("wholemessage").getAsString();
		}

		public String getHeadLine() {
			return headLine;
		}

		public void setHeadLine(String headLine) {
			this.headLine = headLine;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getShortMessage() {
			return shortMessage;
		}

		public void setShortMessage(String shortMessage) {
			this.shortMessage = shortMessage;
		}

		public String getWholeMessage() {
			return wholeMessage;
		}

		public void setWholeMessage(String wholeMessage) {
			this.wholeMessage = wholeMessage;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			News other = (News) obj;
			if (date == null) {
				if (other.date != null) return false;
			} else if (!date.equals(other.date)) return false;
			if (headLine == null) {
				if (other.headLine != null) return false;
			} else if (!headLine.equals(other.headLine)) return false;
			if (id == null) {
				if (other.id != null) return false;
			} else if (!id.equals(other.id)) return false;
			if (imageUrl == null) {
				if (other.imageUrl != null) return false;
			} else if (!imageUrl.equals(other.imageUrl)) return false;
			if (shortMessage == null) {
				if (other.shortMessage != null) return false;
			} else if (!shortMessage.equals(other.shortMessage)) return false;
			if (wholeMessage == null) {
				if (other.wholeMessage != null) return false;
			} else if (!wholeMessage.equals(other.wholeMessage)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "{\"headLine\":\"" + headLine + "\", \"date\":\"" + date + "\", \"id\":\"" + id + "\", \"imageUrl\":\"" + imageUrl + "\", \"shortMessage\":\"" + shortMessage + "\", \"wholeMessage\":\"" + wholeMessage + "\"}";
		}
	}
}
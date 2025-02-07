package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message {
	private Date date = new Date();
	private String from;
	private String to;
	private String text;
	private String room;

	public Message(String from, String to, String text, String room) {
		this.from = from;
		this.to = to;
		this.text = text;
		this.room = room;
	}

	public String toJSON() {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.fromJson(s, Message.class);
	}
	
	@Override
	public String toString() {
		String res = "";
		if (to == null && room == null) {
			res = new StringBuilder().append("public -> [").append(date)
					.append(", From: ").append(from)
					.append("] ").append(text)
					.toString();
		}else if (room == null){
			res = new StringBuilder().append("private -> [").append(date)
					.append(", From: ").append(from).append(", To: ").append(to)
					.append("] ").append(text)
					.toString();
		}else{
			res = new StringBuilder().append("ChatRoom -> [").append(date)
					.append(", From: ").append(from).append(", Room: ").append(room)
					.append("] ").append(text)
					.toString();
		}

		return res;
	}

	public int send(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		try (OutputStream os = conn.getOutputStream()) {
			String json = toJSON();
			os.write(json.getBytes(StandardCharsets.UTF_8));
			return conn.getResponseCode(); // 200?
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRoom() { return room;}

	public void setRoom(String room) { this.room = room;}
}

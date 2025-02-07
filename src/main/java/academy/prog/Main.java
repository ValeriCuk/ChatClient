package academy.prog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter your login: ");
			String login = scanner.nextLine();

			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();
			System.out.println(getInstructions());
            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				String user = null;
				if (text.isEmpty()) {
					int res = logout(login);
					System.out.println(res == 200 ? "You logged out successfully" : "You are not logged out");
					break;
				}else if (text.contains("#online-users")) {
					System.out.println("Get a list of users online");
					System.out.println(getOnlineUsers());

				}else if ((user = getUserIsOnline(text)) != null){
					System.out.println(getUserStatus(user));
				}else{
					String to = getMessageRecipient(text);
					String room = null;
					String roomMovement = "";
					if (to == null && getRoomChat(text) != null) {
						room = getRoomChat(text);
						roomMovement = "?movement=" + checkRoomMovement(text);
					}
					Message m = new Message(login, to, text, room);
					String url = Utils.getURL() + "/add" + roomMovement;
					int res = m.send(url);

					if (res != 200) { // 200 OK
						System.out.println("HTTP error occurred: " + res);
						return;
					}
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private static String getInstructions(){
		StringBuilder sb = new StringBuilder();
		sb.append("Chat Usage Instructions:\n")
				.append("\t1) To send private messages, use @recipientName.\n")
				.append("\t2) To subscribe to a chat room \n")
				.append("\t(to receive and send messages within the room), use in$roomName.\n")
				.append("\t3) To unsubscribe from a chat room, use out$roomName.\n")
				.append("\t4) If both in(out)$roomName and @recipientName are used in a message,\n")
				.append("\tignoring the room.\n")
				.append("\t5) To exit the chat, send an empty message.\n")
				.append("\t6)To get a list of online users, send #online-users.\n")
				.append("\t7)To check if a specific user is online, send #userName.\n")
				.append("Enjoy your chat!\n\n");
		return sb.toString();
	}

	private static String checkRoomMovement(String message) {
		Pattern patternRoom = Pattern.compile("(in|out)\\$(\\S+)");
		Matcher matcher = patternRoom.matcher(message);
		return matcher.find() ? matcher.group(1) : null;
	}


	private static String getRoomChat(String message) {
		Pattern patternRoom = Pattern.compile("\\$(\\S+)");
		Matcher matcher = patternRoom.matcher(message);
		return matcher.find() ? matcher.group(1) : null;
	}

	private static String getMessageRecipient(String message) {
		Pattern patternTo = Pattern.compile("@(\\S+)");
		Matcher matcher = patternTo.matcher(message);
		return matcher.find() ? matcher.group(1) : null;
	}

	private static String getUserIsOnline(String message) {
		Pattern patternTo = Pattern.compile("#(\\S+)");
		Matcher matcher = patternTo.matcher(message);
		String res = matcher.find() && !matcher.group(1).equals("#online-users") ? matcher.group(1) : null;
		return res;
	}

	private static String getUserStatus(String user) throws IOException{
		String onlineUsers = getOnlineUsers();
		String result = onlineUsers.contains(user) ? (user + " -> online") : (user + " -> offline");
		return result;
	}

	private static int logout(String user) throws IOException{
		URL obj = new URL(Utils.getURL() + "/logout?username=" + user);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setDoOutput(true);
		return conn.getResponseCode();
	}

	private static String getOnlineUsers()  throws IOException{
		URL obj = new URL(Utils.getURL() + "/users-online");
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		if (conn.getResponseCode() == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append(System.lineSeparator());
			}
			in.close();

			return sb.toString();

		}else {
			System.out.println("Error: " + conn.getResponseCode());
			return "\nNo one online\n";
		}
	}
}

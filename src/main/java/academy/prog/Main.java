package academy.prog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
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
			System.out.println("To get a list of online users type: \n#online-users\n");
            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) {
					int res = logout(login);
					System.out.println(res == 200 ? "You logged out successfully" : "You are not logged out");
					break;
				}else if (text.contains("#online-users")){
					System.out.println("Get a list of users online");
					System.out.println(getOnlineUsers());
				}
				String to = getMessageRecipient(text);
				Message m = new Message(login, to, text);
				int res = m.send(Utils.getURL() + "/add");

				if (res != 200) { // 200 OK
					System.out.println("HTTP error occurred: " + res);
					return;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private static String getMessageRecipient(String message) {
		Pattern pattern = Pattern.compile("@(\\S+)");
		Matcher matcher = pattern.matcher(message);
		return matcher.find() ? matcher.group(1) : null;
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

package academy.prog;

import java.io.IOException;
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

            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) break;
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
}

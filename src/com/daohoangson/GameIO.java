package com.daohoangson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameIO {
	private PrintWriter out;
	private BufferedReader in;

	final public static int DEFAULT_PORT = 666;

	public GameIO(Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		GameIO.debug("GameIO created", 2);
	}

	public void write(GameMessage m) throws IOException {
		out.print(m.prepare());
		out.flush();
		GameIO.debug("Sent:\n" + m.toString(), 3);
	}

	public GameMessage read() throws IOException {
		GameMessage m = new GameMessage();
		String line;

		do {
			line = in.readLine();
			String[] parts = line.split(" ");

			if (parts.length == 2
					&& parts[1].equals(GameMessage.VERSION_STRING)) {
				m.setCode(parts[0]);
			}

		} while (m.getCode() == 0);

		while (true) {
			line = in.readLine();

			if (line.length() > 0) {
				m.addParam(line);
			} else {
				// empty line found
				if (m.getParamAsInt("Params-Count", -1) == -1) {
					// stop immediately if no lines count specified
					break;
				} else {
					// ignore it
				}
			}

			if (m.getParamsCount() == m.getParamAsInt("Params-Count")) {
				// stop if number of params equals to Params-Count
				break;
			}
		}

		GameIO.debug("Received:\n" + m.toString(), 3);
		return m;
	}

	public void writeOK() throws IOException {
		GameMessage m = new GameMessage(GameMessage.OK);
		write(m);
	}

	public void writeError(int errorCode) throws IOException {
		GameMessage m = new GameMessage(GameMessage.ERROR);
		m.addParam("Error-Code", errorCode);
		write(m);
	}

	public static void debug(String message) {
		GameIO.debug(message, 0);
	}

	public static void debug(String message, int level) {
		int maxLevel = 4;
		if (level < maxLevel) {
			System.out.println(message);
		}
	}
}

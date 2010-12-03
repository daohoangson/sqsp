package com.daohoangson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * IO Manager via socket
 * 
 * @author Dao Hoang Son
 * 
 */
public class GameIO {
	/**
	 * The output. Initialized by constructor
	 */
	private PrintWriter out;
	/**
	 * The input. Initialized by constructor
	 */
	private BufferedReader in;
	/**
	 * The default port for the whole system (server and client)
	 */
	final public static int DEFAULT_PORT = 22222; // Satan's number >:)
	public GameMessage lastSentMessage = null;
	public GameMessage lastReceivedMessage = null;

	/**
	 * Constructs from a {@linkplain Socket socket} from server or client
	 * 
	 * @param socket
	 *            the socket connection
	 * @throws IOException
	 *             if an IOException occurs in middle of its operation
	 */
	public GameIO(Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		GameIO.debug("GameIO created", 2);
	}

	/**
	 * Writes a {@linkplain GameMessage message} to output
	 * 
	 * @param m
	 *            the message
	 * @throws IOException
	 */
	public void write(GameMessage m) throws IOException {
		out.print(m.prepare());
		out.flush();
		lastSentMessage = m;
		GameIO.debug("Sent:\n" + m.toString(), 5);
	}

	/**
	 * Reads a {@linkplain GameMessage message} from input. It will wait until a
	 * message is read
	 * 
	 * @return the message
	 * @throws IOException
	 */
	public GameMessage read() throws IOException {
		GameMessage m = new GameMessage();
		String line;

		// reads the message code
		do {
			line = in.readLine();
			String[] parts = line.split(" ");

			if (parts.length == 2
					&& parts[1].equals(GameMessage.VERSION_STRING)) {
				m.setCode(parts[0]);
			}

		} while (m.getCode() == 0);

		// reads parameters
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

		lastReceivedMessage = m;
		GameIO.debug("Received:\n" + m.toString(), 5);
		return m;
	}

	/**
	 * Writes an OK message. This is a wrapper method
	 * 
	 * @throws IOException
	 * @see GameMessage
	 * @see GameMessage#OK
	 */
	public void writeOK() throws IOException {
		GameMessage m = new GameMessage(GameMessage.OK);
		write(m);
	}

	/**
	 * Writes an Error message. This is a wrapper method
	 * 
	 * @param errorCode
	 *            the error code
	 * @throws IOException
	 * @see GameMessage
	 * @see GameMessage#ERROR
	 */
	public void writeError(int errorCode) throws IOException {
		GameMessage m = new GameMessage(GameMessage.ERROR);
		m.addParam("Error-Code", errorCode);
		write(m);
	}

	/**
	 * Outputs debug information via {@link GameIO#debug(String, int)} with a
	 * level of 0
	 * 
	 * @param message
	 *            the debug information
	 */
	public static void debug(String message) {
		GameIO.debug(message, 0);
	}

	/**
	 * Outputs debug information to standard output device. Only send output if
	 * the debug level is lower than the inner configured level
	 * 
	 * @param message
	 *            the debug information
	 * @param level
	 *            the debug level
	 */
	public static synchronized void debug(String message, int level) {
		int maxLevel = 5;
		if (level <= maxLevel) {
			System.out.println(message);
		}
	}
}

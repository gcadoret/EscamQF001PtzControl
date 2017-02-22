package escam;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscamQF001 {

	protected static Pattern patternSession = Pattern.compile(".*\"SessionID\" : \"([0-9A-Za-z]+)\".*");
	int ip1 = 192;
	int ip2 = 168;
	int ip3 = 0;
	int ip4 = 101;

	int port = 34567;

	String sessionId = "";
	Socket smtpSocket = null;
	DataOutputStream os = null;
	DataInputStream is = null;
	byte[] sessionByte;

	public EscamQF001(int ip1, int ip2, int ip3, int ip4, int port) {
		super();
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.ip3 = ip3;
		this.ip4 = ip4;
		this.port = port;
	}

	public void findCenter() throws IOException {
		/*
		 * actionDirection(STEP8, DirectionLeft, true); long start =
		 * System.currentTimeMillis(); BufferedReader d = new BufferedReader(new
		 * InputStreamReader(is)); String answer = d.readLine(); answer =
		 * d.readLine(); while (answer == null || answer.equals("")) { answer =
		 * d.readLine(); } System.out.println("hhhh" +
		 * Tool.bytesToHex(answer.getBytes()) + "kkkk"); long end =
		 * System.currentTimeMillis();
		 * 
		 * actionDirection(STEP8, DirectionRight, true); System.err.println(end
		 * - start);
		 * 
		 */
		actionDirection(Step.Step8, Direction.DirectionLeft, true);
		byte[] header = { (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x78, 0x05 };
		String message = "{ \"Name\" : \"OPPTZControl\", \"OPPTZControl\" : { \"Command\" : \"Center\"}, \"SessionID\" : \""
				+ sessionId + "\" }";
		byte[] connection = Tool.makeMessage(header, message, sessionByte);
		String answer = requestAndAnswer(connection, "FIND CENTER");
	}

	public void actionDirection(Step step, Direction direction, int time) {
		actionDirection(step, direction, true);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		actionDirection(step, direction, false);

	}

	private void actionDirection(Step step, Direction direction, boolean start) {
		byte[] header = { (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x78, 0x05 };
		String message = "{ \"Name\" : \"OPPTZControl\", \"OPPTZControl\" : { \"Command\" : \"" + direction
				+ "\", \"Parameter\" : { \"AUX\" : { \"Number\" : 0, \"Status\" : \"On\" }, \"Channel\" : 0, \"MenuOpts\" : \"Enter\", \"POINT\" : { \"bottom\" : 0, \"left\" : 0, \"right\" : 0, \"top\" : 0 }, \"Pattern\" : \"SetBegin\", \"Preset\" : "
				+ (start ? "65535" : "-1") + ", \"Step\" : " + step.getValue()
				+ "\", \"Tour\" : 0 } }, \"SessionID\" : \"" + sessionId + "\" }";
		byte[] connection = Tool.makeMessage(header, message, sessionByte);

		requestNoAnswer(connection, "actionDirection " + direction);
	}

	public void startPTZ() {
		byte[] header = { (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x82, 0x05 };
		String message = "{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Start\", \"Parameter\" : { \"Channel\" : 0, \"CombinMode\" : \"NONE\", \"StreamType\" : \"Main\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \""
				+ sessionId + "\" }";
		byte[] connection = Tool.makeMessage(header, message, sessionByte);

		String answer = requestAndAnswer(connection, "Start PTZ");

	}

	public void stopPTZ() {
		byte[] header = { (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x82, 0x05 };
		String message = "{ \"Name\" : \"OPMonitor\", \"OPMonitor\" : { \"Action\" : \"Stop\", \"Parameter\" : { \"Channel\" : 0, \"CombinMode\" : \"NONE\", \"StreamType\" : \"Main\", \"TransMode\" : \"TCP\" } }, \"SessionID\" : \""
				+ sessionId + "\" }";
		byte[] connection = Tool.makeMessage(header, message, sessionByte);

		String answer = requestAndAnswer(connection, "Stop PTZ");

	}

	public void keepAlive() {
		String message = "{ \"Name\" : \"KeepAlive\", \"SessionID\" : \"" + sessionId + "\" }";
		byte[] header = { (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee, 0x03 };
		byte[] connection = Tool.makeMessage(header, message, sessionByte);
		String name = "KeepAlive";
		String answer = requestAndAnswer(connection, name);
		System.out.println(answer);
	}

	private String requestAndAnswer(byte[] connection, String name) {
		requestNoAnswer(connection, name);
		BufferedReader d = new BufferedReader(new InputStreamReader(is));
		String answer = "";
		try {
			answer = d.readLine();
			byte[] output = answer.getBytes();
			Tool.logBytes("Answer " + name, output);

		} catch (IOException e) {

			e.printStackTrace();
		}
		System.out.println();
		return answer;
	}

	private void requestNoAnswer(byte[] connection, String name) {
		Tool.logBytes(name, connection);
		try {
			os.write(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connect(String login, String encryptedPassword) {

		// Initialization section:
		// Try to open a socket on port 25
		// Try to open input and output streams
		try {
			InetAddress ip = InetAddress.getByAddress(new byte[] { (byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4 });
			smtpSocket = new Socket(ip, port);

			os = new DataOutputStream(smtpSocket.getOutputStream());
			is = new DataInputStream(smtpSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: hostname" + e.getMessage());
		}
		if (smtpSocket != null && os != null && is != null) {

			String message = "{ \"EncryptType\" : \"MD5\", \"LoginType\" : \"DVRIP-Web\", \"PassWord\" : \""
					+ encryptedPassword + "\", \"UserName\" : \"" + login + "\" }";

			byte[] header = { (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					(byte) 0xe8, 0x03 };

			byte[] connection = Tool.makeMessage(header, message);
			String answer = requestAndAnswer(connection, "StartSession");

			Matcher m = patternSession.matcher(answer);
			if (m.matches()) {
				sessionId = m.group(1);
				System.out.println("Session ID is " + sessionId);
				String session = sessionId.replace("0x", "");
				sessionByte = Tool.hexStringToByteArray(session);

			}
			System.out.println();
		}
	}
}

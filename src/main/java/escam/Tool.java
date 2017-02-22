package escam;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Tool {
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static void logBytes(String name, byte[] connection) {
		System.out.print(name + " : ");
		for (byte b : connection) {
			System.out.format("0x%x ", b);
		}
		System.out.println();
		try {
			System.out.println(new String(connection, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String byteToString(byte[] _bytes) {
		String file_string = "";

		for (int i = 0; i < _bytes.length; i++) {
			file_string += (char) _bytes[i];
		}

		return file_string;
	}

	public static byte[] appendByteArrays(byte[] one, byte[] two) {
		byte[] combined = new byte[one.length + two.length];

		for (int i = 0; i < combined.length; ++i) {
			combined[i] = i < one.length ? one[i] : two[i - one.length];
		}
		return combined;
	}

	public static byte[] makeMessage(byte[] header, String message, byte[] session) {
		byte[] byteStart = { (byte) 0xff };
		byte[] startHeader = appendByteArrays(byteStart, session);
		byte[] globalHeader = appendByteArrays(startHeader, header);
		return makeMessage(globalHeader, message);

	}

	public static byte[] makeMessage(byte[] header, String message) {
		byte[] bytesLength = ByteBuffer.allocate(4).putInt(message.length() + 1).array();
		bytesLength = reverse(bytesLength);

		byte[] messageByte = message.getBytes();

		byte[] b1 = appendByteArrays(header, bytesLength);
		byte[] b2 = appendByteArrays(b1, messageByte);
		byte[] bytesEnd = { 0x0a };
		byte[] b3 = appendByteArrays(b2, bytesEnd);
		return b3;

	}

	public static byte[] reverse(byte[] validData) {
		for (int i = 0; i < validData.length / 2; i++) {
			byte temp = validData[i];
			validData[i] = validData[validData.length - i - 1];
			validData[validData.length - i - 1] = temp;
		}
		return validData;
	}
}

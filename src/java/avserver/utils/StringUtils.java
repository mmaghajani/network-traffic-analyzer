/*** In The Name of Allah ***/
package avserver.utils;

/**
 * Some string utility methods.
 */
public class StringUtils {
	
	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	/**
	 * Returns a hex string representation of the given byte[].
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	/**
	 * Returns a byte[] converted from the given hex string.
	 */
	public static byte[] hexToByteArray(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
					+ Character.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}
	
	/**
	 * Returns an escaped version of the given text, safe for using inside HTML documents.
	 * This is based on the following OWASP article:
	 * https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.231_-_HTML_Escape_Before_Inserting_Untrusted_Data_into_HTML_Element_Content
	 */
	public static String escapeHTML(String text) {
		StringBuilder safeText = new StringBuilder(text.length());
		for (char c: text.toCharArray()) {
			switch (c) {
				case '&':
					safeText.append("&amp;");
					break;
				case '<':
					safeText.append("&lt;");
					break;
				case '>':
					safeText.append("&gt;");
					break;
				case '"':
					safeText.append("&quot;");
					break;
				case '\'':
					safeText.append("&#x27");
					break;
				case '/':
					safeText.append("&#x2F");
					break;
				default:
					safeText.append(c);
			}
		}
		return safeText.toString();
	}
}

/*** In The Name of Allah ***/
package avserver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for some network operations.
 */
public class INet {
	
	/**
	 * Does a best-effort attempt to get the originating valid IPv4 address 
	 * of the requesting client. Note that this method does not guarantee to 
	 * return a valid IPv4 address and could also return IPs from the reserved 
	 * ranges (e.g. 192.168.10.1, 127.0.0.1, etc)
	 */
	public static String getIP(HttpServletRequest rqst) {
		// First, handle the special case of the standardized 'Forwarded' header
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded
		String ip;
		String header = rqst.getHeader("Forwarded");
		if (header != null && header.length() > 0 && !header.equalsIgnoreCase("unknown")) {
			Scanner sc = new Scanner(header).useDelimiter(";");
			while (sc.hasNext()) {
				String keyValue = sc.next().trim();
				if (keyValue.startsWith("for=")) {
					ip = keyValue.substring(4);
					if (isValidIPv4(ip)) {
						//Logger.info("GET IP USING Forwarded: " + header);
						return ip;
					}
				}
			}
		}
		// list of possible HTTP headers to obtain originating IP address
		String[] headers = {"X-Forwarded-For", "REMOTE_ADDR",
			"HTTP_FORWARDED", "HTTP_FORWARDED_FOR",
			"HTTP_X_FORWARDED", "HTTP_X_FORWARDED_FOR",
			"Proxy-Client-IP", "X-ProxyUser-IP",
			"WL-Proxy-Client-IP", "HTTP_CLIENT_IP"
		};
		for (int i = 0; i < headers.length; ++i) {
			header = rqst.getHeader(headers[i]);
			if (header != null && header.length() > 0 && !header.equalsIgnoreCase("unknown")) {
				// some headers can hold a comma seperated list of IPs:
				// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For
				Scanner sc = new Scanner(header).useDelimiter(",");
				while (sc.hasNext()) {
					ip = sc.next().trim();
					if (isValidIPv4(ip)) {
						//Logger.info("GET IP USING " + headers[i] + ": " + header);
						return ip;
					}
				}
			}
		}
		// if none of the above headers worked, then ...
		ip = rqst.getRemoteHost();
		if (isValidIPv4(ip)) {
			//Logger.info("GET IP USING rqst.getRemoteHost()");
			return ip;
		}
		//Logger.info("GET IP USING rqst.getRemoteAddr()");
		return rqst.getRemoteAddr();
	}

	/**
	 * Checks whether the given string represents a valid IPv4 or not. 
	 * This method also handles IPv6 addresses that are IPv4-compatible.
	 */
	public static boolean isValidIPv4(String ip) {
		try {
			InetAddress adrs = null;
			try {
				InetAddress ipv6 = Inet6Address.getByName(ip);
				if (ipv6 instanceof Inet6Address) {
					if (((Inet6Address) ipv6).isIPv4CompatibleAddress()) {
						adrs = Inet4Address.getByAddress(Arrays.copyOfRange(ipv6.getAddress(), 12, 16));
					} else {
						throw new UnknownHostException();
					}
				} else {
					throw new UnknownHostException();
				}
			} catch (UnknownHostException ex) {
				adrs = Inet4Address.getByName(ip);
			}
			if (adrs.isSiteLocalAddress()) {
				return false;
			}
			if (adrs.isLoopbackAddress()) {
				return true; // should be false (just for testing)
			}
		} catch (UnknownHostException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the country name associated with the given IP address. 
	 * This method uses the free IP-API service. For more info and
	 * documentation refer to: http://ip-api.com/docs/api:newline_separated
	 */
	public static String getCountry(String ip) {
		try {
			URL url = new URL("http://ip-api.com/line/" + ip);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			try (BufferedReader response = new BufferedReader(
					new InputStreamReader(conn.getInputStream()))) {
				if (response.readLine().equals("success")) {
					String name = response.readLine();
					String code = response.readLine();
					Logger.info("GET COUNTRY RESPONSE = " + name + " (" + code + ')');
					return name + " (" + code + ')';
				} else {
					Logger.info("GET COUNTRY RESPONSE = fail: " + response.readLine());
				}
			} finally {
				conn.getInputStream().close();
				conn.disconnect();
			}
		} catch (MalformedURLException | ConnectException ex) {
			Logger.warn("GET-COUNTRY: " + ex.toString());
		} catch (IOException ex) {
			Logger.warn("GET-COUNTRY: " + ex.toString());
		}
		return "N/A";
	}
}

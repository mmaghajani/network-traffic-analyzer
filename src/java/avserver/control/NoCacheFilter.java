/** * In The Name of Allah ** */
package avserver.control;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter to set browser cache management headers.
 */
public class NoCacheFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		httpResponse.setDateHeader("Expires", 0); // Proxies.
		//try {
		chain.doFilter(request, response);
		//} catch (Exception ex) {
		//	Logger.error(ex);
		//}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOP
	}

	@Override
	public void destroy() {
		// NOP
	}
}

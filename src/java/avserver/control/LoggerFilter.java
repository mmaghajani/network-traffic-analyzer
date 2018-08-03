/** * In The Name of Allah ** */
package avserver.control;

import avserver.utils.INet;
import avserver.utils.Logger;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter intercepts and decorates any errors appropriately.
 */
public class LoggerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        long recieve = System.currentTimeMillis();
        //try {
        chain.doFilter(request, response);
        //} catch (Exception ex) {
        //    Logger.error(ex);
        //}
        int time = (int) (System.currentTimeMillis() - recieve);
        int status = httpResponse.getStatus();
        Logger.info("URI = " + httpRequest.getRequestURI() +
					"  |  STATUS = " + status + "  |  TIME = " + time + " ms" +
					"  |  IP = " + INet.getIP(httpRequest) +
					"  |  User-Agent = " + httpRequest.getHeader("User-Agent"));
    }

    /**
     * Initialize method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }
}

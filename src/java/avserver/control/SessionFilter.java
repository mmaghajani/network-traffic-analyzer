/** * In The Name of Allah ** */
package avserver.control;

import avserver.model.Database;
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
import javax.servlet.http.HttpSession;

/**
 * Filter for handling user sessions.
 */
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String reqURI = request.getRequestURI();
        HttpSession session = request.getSession(true);
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            user = Database.getGuestUser();
//            session.setAttribute("user", user);
//	        session.setMaxInactiveInterval(10 * 60); // 10 mins
//            Logger.info("SESSION-FILTER: NO USER INFO;  SETTING AS GUEST.");
//        }
        String httpReferer = request.getHeader("Referer");
        String forwarder = (String) request.getAttribute("javax.servlet.forward.request_uri");

        /**
         * Perform checks based on the request URI
         */
        if (reqURI.endsWith("/upload")) {
            if (httpReferer == null || httpReferer.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }
        //
        if (reqURI.endsWith("report.jsp")) {
            if (httpReferer == null || !hasAttribute(session, "fileSize")
                    || !hasAttribute(session, "fileName") || !hasAttribute(session, "fileSizeStr")
                    || !hasAttribute(session, "contentType") || !hasAttribute(session, "sha256Digest")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }
        //
        if (reqURI.endsWith("/doSearch")) {
            if (!hasAttribute(session, "fileName") || !hasAttribute(session, "sha256Digest")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }
        //
        if (reqURI.endsWith("/doScan")) {
            if (//forwarder == null ||
                    !hasAttribute(session, "fileName")
                    || !hasAttribute(session, "fileSize")
                    || !hasAttribute(session, "sha256Digest")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }
        //
//        if (reqURI.endsWith("feedback.jsp")) {
//            if (user.UNAME.equals("guest")) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_UNAUTHORIZED);
//                request.getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
//                return;
//            }
//        }
        // else, all is fine so continue processing the request
        try {
            chain.doFilter(req, res);
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }

    private boolean hasAttribute(HttpSession session, String name) {
        return session.getAttribute(name) != null;
    }

    /**
     * Initialization method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        // nothing yet ...
    }
}

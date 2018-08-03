/*** In The Name of Allah ***/
package avserver.control;

import avserver.config.Config;
import avserver.model.Database;
import avserver.model.Report;
import avserver.model.Analyse;
import avserver.utils.Logger;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A Servlet to search for a given file based on the SHA-256 digest.
 */
public class SearchFileServlet extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);

		String fileName = (String) session.getAttribute("fileName");
		String sha256Digest = (String) session.getAttribute("sha256Digest");
		//long fileSize = Long.parseLong((String) session.getAttribute("fileSize"));
		
		Logger.log("========================================");
		Logger.info("DO SEARCH of " + fileName );
		Logger.info("USING " + sha256Digest);
		
		// search for any previous scans regarding this file
		try {
			ArrayList<Analyse> scans = Database.searchFile(sha256Digest);

			if (scans.isEmpty()) {
				// this file has never been scanned by our service;
				// perform a new scan ...
				Logger.info("No Reports Found! Forwarding request to DoScan ...");
				request.getRequestDispatcher("/doScan").forward(request, response);
			} else {
				// this file has previously been scanned by our service
				Logger.info("Report Found.");
				ArrayList<Report> reports = Database.getReports(scans.get(0));
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Report", "old");
				boolean rescan = isRescanRequired(reports);
				response.setHeader("Rescan", Boolean.toString(rescan));
				if (!rescan)
					new File(Config.UPLOAD_DIR + fileName).delete();
				Logger.info("Showing old report " + (rescan ? "with rescan" : "without rescan"));
				response.getWriter().write(new Gson().toJson(reports));
			}
		} catch (SQLException ex) {
			Logger.error(ex);
		}
	}
	
	/**
	 * Checks whether a rescan is useful for the given reports.
	 * This is done by comparing the time of the scans 
	 * with the time of the anti-viruses last update.
	 */
	private boolean isRescanRequired(ArrayList<Report> reports) {
            return true;
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}

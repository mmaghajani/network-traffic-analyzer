/*** In The Name of Allah ***/
package avserver.control;

import avserver.config.Config;
import avserver.model.Database;
import avserver.model.Detection;
import avserver.model.Report;
import avserver.model.Analyse;
import avserver.utils.INet;
import avserver.utils.Logger;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet to perform the scan using multiple anti-viruses.
 */
public class ScanServlet extends HttpServlet {
	
	private String countryName;

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
//		User user = (User) session.getAttribute("user");

		long fileSize = (Long) session.getAttribute("fileSize");
		String fileName = (String) session.getAttribute("fileName");
		String sha256Digest = (String) session.getAttribute("sha256Digest");
		
		Logger.log("========================================");
		Logger.info("DO SCAN of " + fileName);
		
		// this file has never been scanned by our service ... do it!
		String ip = INet.getIP(request);
		Logger.info("IP = " + ip);

		// start thread to get country name
		countryName = "N/A";
		Thread getCountryName = new Thread(new Runnable() {
			@Override
			public void run() {
				countryName = INet.getCountry(ip);
			}
		});
		getCountryName.start();

		// execute the scan script
		int exitValue;
		StringBuilder exitMessage = null;
		Process scanProcess = null;
		try {
			String savePath = Config.UPLOAD_DIR + fileName;
			String[] scanCmd = new String[] { Config.SCAN_SCRIPT, savePath };
			scanProcess = Runtime.getRuntime().exec(scanCmd);
			// and wait for the scan process to finish ...
			exitValue = scanProcess.waitFor();
		} catch (InterruptedException ex) {
			Logger.warn(ex);
			exitValue = -1;
			exitMessage = new StringBuilder(ex.toString());
		} catch (IOException ex) {
			Logger.error(ex);
			exitValue = -2;
			exitMessage = new StringBuilder(ex.toString());
		}

		// if scan process ended normally ...
		if (exitValue == 0) {
			Logger.info("SCAN PROCESS FINISHED OK.");
			// wait at most 2 seconds until the getCountry request finishes
			try {
				getCountryName.join(2000);
				Logger.info("COUNTRY = " + countryName);
			} catch (InterruptedException ex) {
				Logger.warn(ex);
			}
			// perform the scan and get the AV reports
			ArrayList<Report> reports;
			try {
				reports = generateReports(fileName, (int) fileSize, sha256Digest, ip, scanProcess);
			} catch (SQLException | IOException ex) {
				Logger.error(ex);
				return;
			}

			// show resulting reports
			Logger.info("SHOW REPORT ...");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Report", "new");
			response.setHeader("Rescan", "false");
			response.getWriter().write(new Gson().toJson(reports));

		} else {
			// scan process failed!
			Logger.error("SCAN PROCESS EXITED WITH CODE = " + exitValue);
			if (exitValue > 0) {
				// Read its std-err and log it.
				BufferedReader stderr = new BufferedReader(
					new InputStreamReader(scanProcess.getErrorStream()));
				exitMessage = new StringBuilder();
				String line;
				while ((line = stderr.readLine()) != null) 
					exitMessage.append("    ").append(line).append('\n');
				Logger.error("SCAN-SCRIPT-ERR:\n" + exitMessage.toString());
			} else {
				// exit value is negative; script exited with exception
				Logger.error("SCAN-SCRIPT-ERR:  " + exitMessage.toString());
			}
			// show error page to client
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			request.setAttribute("javax.servlet.error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			request.getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
		}
	}
	
	/**
	 * Generates a list of reports based on the output of the scan-process.
	 * This will insert the scan info and the generated reports into the database.
	 * This method will also rename the scanned file according to the scan-ID for archival.
	 */
	private ArrayList<Report> generateReports(String fileName, int fileSize, String sha256Digest, String ip, Process scanProcess) 
			throws SQLException, IOException {
		// insert scan information into database
                byte[] hash = {0x12, 0x22};
                Timestamp t = new Timestamp(System.currentTimeMillis());
//                User user = new User(1, "karim", hash, "Agha karim", t, t);
		Analyse scan = new Analyse(-1, new Timestamp(System.currentTimeMillis()), ip, 
							 countryName, fileName, (int) fileSize, sha256Digest);
		long scanID = Database.insertAnalyse(scan);
		scan = scan.updateID(scanID);
		Logger.info("Scan information inserted with ID = " + scanID);
		
		// read std-output of scan-process line by line
		ArrayList<Report> reportsList = new ArrayList<>();
		String line; 
		BufferedReader stdout = new BufferedReader(
				new InputStreamReader(scanProcess.getInputStream()));
		while ((line = stdout.readLine()) != null) {
			// process each line
			Scanner scanner = new Scanner(line).useDelimiter("#");
                        byte detID = scanner.nextByte();
                        int data_index = scanner.nextInt();
                        double score1 = scanner.nextDouble();
                        double score2 = scanner.nextDouble();
                        double score3 = scanner.nextDouble();
                        double score4 = scanner.nextDouble();
                        // create new report and insert it into database
                        Detection det = Database.getDetection(detID);
                        Report report = new Report(scan, det, data_index, score1, score2, score3, score4);
                        reportsList.add(report);
                        Database.insertReport(report);
                        Logger.info("Report  inserted.");
		}
		return reportsList;
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

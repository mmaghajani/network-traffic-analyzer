/** * In The Name of Allah ** */
package avserver.control;

import avserver.config.Config;
import avserver.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet that processes file-upload request.
 */
public class UploadServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request Servlet request
     * @param response Servlet response
     * @throws ServletException if a Servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // List of view attributes
        Logger.log("========================================");
        Logger.info("UPLOAD REQUEST");
        String fileName = "";
        long sizeInBytes = 0L;
        String contentType = "";
        String sha256Digest = "";
        File uploadedFile = null;

        HttpSession session = request.getSession(false);

        /**
         * Apache Commons FileUpload Usage Guide:
         * https://commons.apache.org/proper/commons-fileupload/using.html
         */
        // Check that we have a file upload request 
        if (ServletFileUpload.isMultipartContent(request)) {
            // Create and configure a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(4 * 1024 * 1024); // 4 MB
            factory.setRepository(new File(Config.TEMP_DIR));

            // Create and configure a new file Upload-Handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(Config.MAX_FILE_SIZE * 3);
            // Process uploaded items
            try {
                List<FileItem> items = upload.parseRequest(request);
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    // handle file uploads
                    switch (item.getFieldName()) {
                        case "file":
                            fileName = item.getName();
                            contentType = item.getContentType();
                            sizeInBytes = item.getSize();
                            Logger.info("Upload Size = " + getFileSize(sizeInBytes));
                            // check fileName length
                            if (fileName.length() > 60) {
                                fileName = fileName.substring(0, 60);
                            }

                            // check for empty uploads
                            if (!fileName.isEmpty() && sizeInBytes > 0L) {
                                // first, escape file-name
                                fileName = escapeFileName(fileName);

                                // check if the upload destination directory exists
                                boolean uploadDirExists = true;
                                uploadedFile = new File(Config.UPLOAD_DIR + fileName);
                                File uploadDir = new File(Config.UPLOAD_DIR);

                                if (!uploadDir.exists()) // if not, then create it
                                {
                                    uploadDirExists = uploadDir.mkdirs();
                                }

                                // Save uploaded file
                                if (uploadDirExists) {
                                    // check if a file with the same name exists
                                    if (uploadedFile.exists()) {
                                        // if so, rename this file
                                        fileName = renameExistingFilename(fileName);
                                        uploadedFile = new File(Config.UPLOAD_DIR + fileName);
                                    }

                                    // save the uploaded file to disk
                                    try {
                                        item.write(uploadedFile);
                                    } catch (Exception ex) {
                                        Logger.error(ex);
                                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                        return;
                                    }
                                }
                            }
                            break;

                        case "submit":
                            break;

                        default:
                            Logger.warn("UNEXPECTED PART = [" + item.getFieldName() + "] !!");
                            break;
                    }
                }
            } catch (FileUploadException ex) {
                Logger.warn("TOO LARGE FILE UPLOAD!");
                response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                return;
            }
        } else {
            // Not a multipart request
            Logger.warn("Invalid Request Type");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (uploadedFile == null) {
            Logger.error("NO [file] PART UPLOADED!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // compute file checksum
        try (InputStream fis = new FileInputStream(uploadedFile)) {
            sha256Digest = DigestUtils.sha256Hex(fis);
        }

        Logger.info("FILE = " + fileName);
        Logger.info("TYPE = " + contentType);
        Logger.info("SIZE = " + getFileSize(sizeInBytes));

        session.setAttribute("fileName", fileName);
        session.setAttribute("fileSize", sizeInBytes);
        session.setAttribute("contentType", contentType);
        session.setAttribute("fileSizeStr", getFileSize(sizeInBytes));
        session.setAttribute("sha256Digest", sha256Digest);

        //String lang = (String) request.getParameter("lang");
        response.sendRedirect("/report.jsp");
    }

    /**
     * Returns a human-readable representation of a given file size.
     */
    private String getFileSize(long size) {
        if (size < 1024) {
            return size + " Bytes";
        }
        if (size < 1024 * 1024) {
            return String.format("%.2f KB", ((float) size) / 1024f);
        }
        if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", ((float) size) / (1024f * 1024f));
        }
        //
        return String.format("%.2f GB", ((float) size) / (1024f * 1024f * 1024f));
    }

    private String renameExistingFilename(String fileName) {
        File file;
        int counter = 2;
        String newFileName = fileName;
        do {
            int dot = fileName.lastIndexOf('.');
            if (dot > 0) {
                newFileName = fileName.substring(0, dot) + ("_" + counter++) + fileName.substring(dot);
            } else {
                newFileName += ("_" + counter++);
            }
            file = new File(Config.UPLOAD_DIR + newFileName);
        } while (file.exists());
        return newFileName;
    }

    private String escapeFileName(String fileName) {
        return fileName.replace(' ', '_').replace('&', '_').replace('+', '_')
                .replace(':', '_').replace('(', '_').replace(')', '_');
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
        return "Servlet to process file upload requests.";
    }// </editor-fold>

}

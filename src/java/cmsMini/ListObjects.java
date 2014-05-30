package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Muhammad Wannous
 */
public class ListObjects extends HttpServlet {

    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JacksonFactory jsonFactory = new JacksonFactory();
    private GoogleCredential credential;

    /**
     *
     * Build and saves a Drive service object authorized with the service accounts.
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException {
        String accountEmail = this.getServletContext().getInitParameter("SERVICE_ACCOUNT_EMAIL");
        String keyFilePath = this.getServletContext().getInitParameter(
                "SERVICE_ACCOUNT_PKCS12_FILE_PATH");
        credential = null;
        java.io.File keyFile;
        try {
            keyFile = new java.io.File(this.getServletContext().getResource(keyFilePath).toURI());
            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(accountEmail)
                    .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
                    .setServiceAccountPrivateKeyFromP12File(keyFile)
                    .build();
        } catch (GeneralSecurityException | IOException | URISyntaxException ex) {
            throw (new ServletException(ex.getCause()));
        }
    }

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
        if (credential != null) {
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("YOUNESmini").build();
            Drive.Files.List listRequest = service.files().list().setQ(
                    "mimeType = 'application/vnd.google-apps.folder'");
            FileList list = listRequest.execute();
            List<File> folders = new ArrayList<>();
            List<File> files = new ArrayList<>();
            folders.addAll(list.getItems());
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet ListObjects</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>List of folders in " + service.getServicePath() + "</h1><br>");
                while (!folders.isEmpty()) {
                    out.println("item: " + folders.get(0).getTitle() + "<br>");
                    listRequest = service.files().list().setQ("'"
                            + folders.remove(0).getId() + "' in parents");
                    list = listRequest.execute();
                    files.addAll(list.getItems());
                    out.println("<ul>");
                    while (!files.isEmpty()) {
                        out.println("<li>");
                        out.println(files.remove(0).getTitle() + "</li>");
                    }
                    out.println("</ul><br>");
                }
                About about = service.about().get().execute();
                out.println("Current user name: " + about.getName() + "<br>");
                out.println("Total quota (bytes): " + about.getQuotaBytesTotal() + "<br>");
                out.println("Used quota (bytes): " + about.getQuotaBytesUsed() + "<br>");
                out.println("</body>");
                out.println("</html>");
            }
        }
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
        return "This is a test Servlet for connecting a Google App Engine application"
                + "with Google Drive using Google Drive API.";
    }// </editor-fold>

}

package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Muhammad Wannous
 */
public class download extends HttpServlet {

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
    HttpSession session = request.getSession();
    ServletContext application = this.getServletContext();
    String fileIdString = request.getParameter("id");
    Map<String, String[]> filesInfo = (Map<String, String[]>) session.getAttribute("filesInfo");
    GoogleCredential credential = (GoogleCredential) application.getAttribute("credential");
    User thisUser = (User) session.getAttribute("userIn");
    String applicationID = application.getInitParameter("APPLICATION_ID");
    if (fileIdString != null
            && !fileIdString.equals("")
            && filesInfo != null
            && !filesInfo.isEmpty()
            && credential != null
            && thisUser != null
            && applicationID != null
            && !applicationID.equals("")) {
      HttpTransport httpTransport = new NetHttpTransport();
      JacksonFactory jsonFactory = new JacksonFactory();
      Drive serviceDrive = new Drive.Builder(httpTransport, jsonFactory, credential)
              .setApplicationName(applicationID).build();
      HttpResponse fileHttpResponse
              = serviceDrive.getRequestFactory().buildGetRequest(new GenericUrl(filesInfo.get(fileIdString)[2]))
              .execute();
      InputStream downloadInputStream = fileHttpResponse.getContent();
      byte[] byteBuffer = new byte[4096];
      ServletOutputStream outStream;
      try (DataInputStream in = new DataInputStream(downloadInputStream)) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filesInfo.get(fileIdString)[0] + "\"");
        int length = 0;
        outStream = response.getOutputStream();
        while ((in != null) && ((length = in.read(byteBuffer)) != -1)) {
          outStream.write(byteBuffer, 0, length);
        }
      }
      outStream.close();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key logKey = KeyFactory.createKey("tableName", "Log");
      Entity logEntity = new Entity("Log", logKey);
      logEntity.setProperty("Action", "Download");
      logEntity.setProperty("User", thisUser.getUserID());
      logEntity.setProperty("File", filesInfo.get(fileIdString)[0]);
      logEntity.setProperty("Time", (new java.util.Date()).toString());
      datastore.put(logEntity);
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
    return "Short description";
  }// </editor-fold>

}

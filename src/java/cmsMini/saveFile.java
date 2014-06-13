package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author Muhammad Wannous
 *
 */
/**
 * @MultipartConfig(fileSizeThreshold=1024*1024*2, maxFileSize=1024*1024*2, maxRequestSize=1024*1024*4)
 */
public class saveFile extends HttpServlet {

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
    ServletContext application = this.getServletContext();
    HttpSession session = request.getSession(true);
    String applicationID = application.getInitParameter("APPLICATION_ID");
    /*The xml file containing the course and instructor information is saved in a folder
     that is shared with us on Google Drive.*/
    /*The credential needs HttpTransport, JacksonFactory, account ID, and KeyFile.*/
    HttpTransport httpTransport = new NetHttpTransport();
    JacksonFactory jsonFactory = new JacksonFactory();
    GoogleCredential credential = (GoogleCredential) application.getAttribute("credential");
    /*This Servlet will give the admin user a list of the xml files in the shared Google Drive to select
     one from them to import the user information from.*/
    CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User) session.getAttribute("userIn");
    String homeFolderId = (String) session.getAttribute("homeFolderId");
    if ((thisCourseDetails == null) || (thisCourse == null)
            || (thisUser == null) || (credential == null)
            || (homeFolderId == null) || homeFolderId.equals("")) {
      System.out.println("saveFile# User, credential, or homeFolder problem!");
      session.setAttribute("infoString", "Not completed!");
    } else {
      Drive serviceDrive = new Drive.Builder(httpTransport, jsonFactory, credential)
              .setApplicationName(applicationID).build();
      if (ServletFileUpload.isMultipartContent(request)) {
        ServletFileUpload upload = new ServletFileUpload();
        try {
          List<FileItem> allFiles = upload.parseRequest(request);
          for (FileItem file : allFiles) {
            File body = new File();
            body.setTitle(file.getName());
            body.setParents(Arrays.asList(new ParentReference().setId(homeFolderId)));
            InputStreamContent contents = new InputStreamContent(null, file.getInputStream());
            Drive.Files.Insert insertRequest = serviceDrive.files().insert(body, contents);
            insertRequest.execute();
          }
          session.setAttribute("infoString", "Upload completed!");
        } catch (FileUploadException ex) {
          ex.printStackTrace(System.out);
        }
      }
    }
    response.sendRedirect("home.jsp");
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

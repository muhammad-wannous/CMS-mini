package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Muhammad Wannous This servlet creates one folder for each user in Google Drive. Folder names are identical to
 * the IDs of the users.
 */
public class createFolders extends HttpServlet {

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
    HttpSession session = request.getSession();
    String applicationID = application.getInitParameter("APPLICATION_ID");
    GoogleCredential credential = (GoogleCredential) application.getAttribute("credential");
    HttpTransport httpTransport = new NetHttpTransport();
    JacksonFactory jsonFactory = new JacksonFactory();
    CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User) session.getAttribute("userIn");
    //To invoke this servlet, the user should be an admin and the course details should have been saved.
    if (((thisCourseDetails == null) || (thisCourse == null)
            || (thisUser == null)) || (!thisUser.getUserRole().endsWith("dmin")) || (credential == null)) {
      System.out.println("createFolders# User or credential problem.");
      session.setAttribute("infoString", "Problem or action not allowed!");
    } else {
      Drive serviceDrive = new Drive.Builder(httpTransport, jsonFactory, credential)
              .setApplicationName(applicationID).build();
      /*Get a list of all folders in Google Drive.*/
      Drive.Files.List listRequest = serviceDrive.files().list().setQ(
              "mimeType = 'application/vnd.google-apps.folder");
      FileList folderList = listRequest.execute();
      List<File> folders = new ArrayList<>();
      folders.addAll(folderList.getItems());
      /*Get a list of all users from Datastore*/
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key userKey = KeyFactory.createKey("tableName", "User");
      Query userQuery = new Query("User", userKey);
      List<Entity> dbUsers = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
      /*Go through the lists, if the folder of the user has been already created then skip, otherwise create it.*/
      for (Entity userEntity : dbUsers) {
        boolean ownFolderExists = false;
        for (File folder : folders) {
          List<ParentReference> parentList = folder.getParents();
          for (ParentReference parent : parentList) {
            if (parent.getIsRoot() && folder.getTitle().equals(userEntity.getProperty("courseUserID"))) {
              
              ownFolderExists = true;
              break;
            }
          }
          if (ownFolderExists) {
            break;
          }
        }
        if (!ownFolderExists) {
          File contentFolder = new File();
          contentFolder.setTitle((String) userEntity.getProperty("courseUserID"));
          contentFolder.setDescription("User folder in CMS-mini");
          contentFolder.setMimeType("application/vnd.google-apps.folder");
          contentFolder.setParents(Arrays.asList(new ParentReference().setId("root")));
          Drive.Files.Insert createFolderInsert = serviceDrive.files().insert(contentFolder);
          createFolderInsert.execute().getId();
        }
      }
      session.setAttribute("infoString", "Folders ready.!");
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

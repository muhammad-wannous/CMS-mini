package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Muhammad Wannous
 */
public class enableAndroid extends HttpServlet {

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
    Date date = new Date();
    String applicationID = application.getInitParameter("APPLICATION_ID");
    /*The credential needs HttpTransport, JacksonFactory, account ID, and KeyFile.*/
    HttpTransport httpTransport = new NetHttpTransport();
    JacksonFactory jsonFactory = new JacksonFactory();
    GoogleCredential credential = (GoogleCredential) application.getAttribute("credential");
    /*If the credential is not set or timed out then recreate it.*/
    if (credential == null) {
      /*Account ID and the key file location are passed in web.xml*/
      String accountEmail = application.getInitParameter("SERVICE_ACCOUNT_EMAIL");
      String keyFilePath = application.getInitParameter(
              "SERVICE_ACCOUNT_PKCS12_FILE_PATH");
      /*We get the account ID and KeyFile from Google App Engine console for our project.*/
      java.io.File keyFile;
      try {
        keyFile = new java.io.File(application.getResource(keyFilePath).toURI());
        credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(accountEmail)
                .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
                .setServiceAccountPrivateKeyFromP12File(keyFile)
                .build();
        application.setAttribute("credential", credential);
      } catch (GeneralSecurityException | URISyntaxException | IOException ex) {
        System.out.println("\nError with credentials.");
        throw new ServletException(ex);
      }
    }
    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(applicationID).build();
    /*This Servlet will give the admin user a list of the xml files in the shared Google Drive to select
     one from them to import the user information from.*/
    CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User) session.getAttribute("userIn");
    if (((credential == null) || (thisCourseDetails == null) || (thisCourse == null)
            || (thisUser == null)) || (!thisUser.getUserRole().endsWith("dmin"))) {
      System.out.println("\nError in addUsers.");
      session.setAttribute("infoString", "Error in enableAndroid!");
    } else {
      File body = new File();
      body.setTitle("users.txt");
      body.setDescription("Created for saving Android users' credentials.");
      body.setMimeType("text/plain");
      body.setParents(Arrays.asList(new ParentReference().setId("root")));
      String textContents = "***\n"
              + "This file is used for authenticating Android users.\n"
              + "***";
      InputStreamContent content = new InputStreamContent("text/plain",
              new BufferedInputStream(IOUtils.toInputStream(textContents)));
      body = service.files().insert(body, content).execute();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key userKey = KeyFactory.createKey("tableName", "User");
      Query userQuery = new Query("User", userKey);
      List<Entity> dbUsers = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
      Property newProperty;
      for (Entity userEntity : dbUsers) {
        newProperty = new Property();
        newProperty.setKey((String) userEntity.getProperty("courseUserID"));
        newProperty.setValue((String) userEntity.getProperty("courseUserPassword"));
        newProperty.setVisibility("PRIVATE");
        service.properties().insert(body.getId(), newProperty).execute();
      }
      session.setAttribute("infoString", "Credentials added for Android App!");
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

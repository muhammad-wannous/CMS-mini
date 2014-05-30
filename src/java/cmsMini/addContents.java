package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Muhammad Wannous
 */
public class addContents extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   * @throws java.net.URISyntaxException
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException, URISyntaxException {
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
      System.out.println("\nError in addContents.");
      session.setAttribute("infoString", "Error in addContents!");
      response.sendRedirect("home.jsp");
    } else {
      try {
        String selectedFile = (String) request.getParameter("selectedContentFile");
        Map<String, String> sharedFiles = (Map<String, String>) session.getAttribute("sharedContentFiles");
        if ((selectedFile != null) && !selectedFile.equals("") && (sharedFiles != null)) {
          /*
           The files we've got this time is a .zip file that is compatible with IMS Content Packaging-1.1.4
           format. We first need to unzip it into the local Drive storage.
           */
          /*First create a folder that have the same name as the file name (if not exists)*/
          Drive.Files.List listRequest = service.files().list().setQ(
                  "mimeType = 'application/vnd.google-apps.folder' "
                  + "and trashed != true "
                  + "and title = '" + selectedFile.substring(0, selectedFile.indexOf(".zip")) + "' ");
          FileList folderList = listRequest.execute();
          String contentFolderId = null;
          if (!folderList.isEmpty()) {
            List<File> folders = new ArrayList<>();
            folders.addAll(folderList.getItems());
            while (!folders.isEmpty()) {
              if (!folders.get(0).getShared()) {
                contentFolderId = folders.get(0).getId();
                break;
              }
              folders.remove(0);
            }
          }
          if (contentFolderId == null) {
            File contentFolder = new File();
            contentFolder.setTitle(selectedFile.substring(0, selectedFile.indexOf(".zip")));
            contentFolder.setDescription("Imported from " + selectedFile + " IMS Contents Packaging 1.1.4");
            contentFolder.setMimeType("application/vnd.google-apps.folder");
            contentFolder.setParents(Arrays.asList(new ParentReference().setId("root")));
            Drive.Files.Insert createFolderInsert = service.files().insert(contentFolder);
            contentFolderId = createFolderInsert.execute().getId();
          }
          /*The folder to contain the decompressed contents is ready (hopefully)*/
          ZipFileToDrive fileToDrive = new ZipFileToDrive(service, contentFolderId, sharedFiles.get(selectedFile));
          fileToDrive.expandZipFileInDrive();
          session.setAttribute("infoString", "Contents imported!");
        } else {
          session.setAttribute("infoString", "Problem in importing contents!");
        }
      } catch (IOException ex) {
        ex.printStackTrace(System.out);
        throw new ServletException(ex);
      }
    }
    response.sendRedirect("home.jsp?ts=" + date.toString());
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
    try {
      processRequest(request, response);
    } catch (URISyntaxException ex) {
      ex.printStackTrace(System.out);
    }
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
    try {
      processRequest(request, response);
    } catch (URISyntaxException ex) {
      ex.printStackTrace(System.out);
    }
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

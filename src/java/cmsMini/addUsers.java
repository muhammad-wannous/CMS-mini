package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Muhammad Wannous
 */
public class addUsers extends HttpServlet {

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
      session.setAttribute("infoString", "Error in addUsers!");
      response.sendRedirect("home.jsp");
    } else {
      try {
        String selectedFile = (String) request.getParameter("selectedFile");
        Map<String, String> sharedFiles = (Map<String, String>) session.getAttribute("sharedFiles");
        if ((selectedFile != null) && !selectedFile.equals("") && (sharedFiles != null)) {

          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          dbFactory.setValidating(true);

          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          DTDEntityResolver dtdEntityResolver = new DTDEntityResolver();
          dtdEntityResolver.setApplication(application);
          dBuilder.setEntityResolver(dtdEntityResolver);
          Document usersDocument = dBuilder.parse(service.getRequestFactory().buildGetRequest(
                  new GenericUrl(sharedFiles.get(selectedFile))).execute().getContent());
          CourseUser newCourseUser = new CourseUser();
          Node userNode, tempNode, tempNode1, tempNode2, membershipNode;
          List<String> courseMembersList = new ArrayList<>();
          Key userKey = KeyFactory.createKey("tableName", "User");
          Entity userEntity = new Entity("User", userKey);
          /*After parsing the user, we should verify that he/she is a member of our course.*/
          /*So, first we check the membership part of the xml file.*/
          NodeList membershipNodeList = usersDocument.getElementsByTagName("membership");
          if (membershipNodeList.getLength() > 0) {
            boolean thismembership;
            for (int i = 0; i < membershipNodeList.getLength(); i++) {
              thismembership = false;
              membershipNode = membershipNodeList.item(i);
              tempNode = membershipNode.getFirstChild();
              while (tempNode != null) {
                switch (tempNode.getNodeName()) {
                  case "sourcedid":
                    tempNode1 = tempNode.getFirstChild();
                    while (tempNode1 != null) {
                      switch (tempNode1.getNodeName()) {
                        case "id":
                          if (tempNode1.getTextContent().trim().equals(thisCourseDetails.getCourseID())) {
                            thismembership = true;
                          }
                          break;
                        default:
                          break;
                      }
                      tempNode1 = tempNode1.getNextSibling();
                    }
                    break;
                  case "member":
                    tempNode1 = tempNode.getFirstChild();
                    while (tempNode1 != null) {
                      switch (tempNode1.getNodeName()) {
                        case "sourcedid":
                          tempNode2 = tempNode1.getFirstChild();
                          while (tempNode2 != null) {
                            switch (tempNode2.getNodeName()) {
                              case "id":
                                if (thismembership) {
                                  courseMembersList.add(tempNode2.getTextContent().trim());
                                }
                                break;
                              default:
                                break;
                            }
                            tempNode2 = tempNode2.getNextSibling();
                          }
                          break;
                        default:
                          break;
                      }
                      tempNode1 = tempNode1.getNextSibling();
                    }
                    break;
                  default:
                    break;
                }
                tempNode = tempNode.getNextSibling();
              }
            }
          }
          NodeList usersNodeList = usersDocument.getElementsByTagName("person");

          /*We need to obtain:
           - User ID <userid>
           - password <userid password>
           - Family name <name><n><family>
           - Given name <name><n><given>
           - Prefix <name><n><prefix>
           - Gender <demographics><gender>
           - Birthday <demographics><bd>
           - E-mail <email>
           - URL <url>
           - Tel <tel>
           - System role <systemrole>
           - Institute role <institutionrole>*/
          for (int i = 0; i < usersNodeList.getLength(); i++) {
            userNode = usersNodeList.item(i);
            tempNode = userNode.getFirstChild();
            /*We will need 2 nodes only <userid> <systemrole>*/
            while (tempNode != null) {
              switch (tempNode.getNodeName()) {
                case "userid":
                  newCourseUser.setCourseUserID(tempNode.getTextContent().trim());
                  newCourseUser.setCourseUserPassword(
                          tempNode.getAttributes().getNamedItem("password").getTextContent().trim());
                  break;
                case "name":
                  tempNode1 = tempNode.getFirstChild();
                  while (tempNode1 != null) {
                    switch (tempNode1.getNodeName()) {
                      case "n":
                        tempNode2 = tempNode1.getFirstChild();
                        while (tempNode2 != null) {
                          switch (tempNode2.getNodeName()) {
                            case "family":
                              newCourseUser.setCourseUserFamilyName(tempNode2.getTextContent().trim());
                              break;
                            case "given":
                              newCourseUser.setCourseUserGivenName(tempNode2.getTextContent().trim());
                              break;
                            case "prefix":
                              newCourseUser.setCourseUserNamePrefix(tempNode2.getTextContent().trim());
                              break;
                            default:
                              break;
                          }
                          tempNode2 = tempNode2.getNextSibling();
                        }
                        break;
                      default:
                        break;
                    }
                    tempNode1 = tempNode1.getNextSibling();
                  }
                  break;
                case "demographics":
                  tempNode1 = tempNode.getFirstChild();
                  while (tempNode1 != null) {
                    switch (tempNode1.getNodeName()) {
                      case "gender":
                        newCourseUser.setCourseUserGender(tempNode1.getTextContent().trim());
                        break;
                      case "bday":
                        newCourseUser.setCourseUserBirthday(tempNode1.getTextContent().trim());
                        break;
                      default:
                        break;
                    }
                    tempNode1 = tempNode1.getNextSibling();
                  }
                  break;
                case "email":
                  newCourseUser.setCourseUserEmail(tempNode.getTextContent().trim());
                  break;
                case "url":
                  newCourseUser.setCourseUserURL(tempNode.getTextContent().trim());
                  break;
                case "tel":
                  newCourseUser.setCourseUserTel(tempNode.getTextContent().trim());
                  break;
                case "systemrole":
                  newCourseUser.setCourseUserSystemRole(
                          tempNode.getAttributes().getNamedItem("systemroletype").getTextContent().trim());
                  break;
                case "institutionrole":
                  newCourseUser.setCourseUserInstitutionRole(
                          tempNode.getAttributes().getNamedItem("institutionroletype").getTextContent().trim());
                  break;
                default:
                  break;
              }
              tempNode = tempNode.getNextSibling();
            }

            for (PropertyDescriptor pd
                    : Introspector.getBeanInfo(newCourseUser.getClass()).getPropertyDescriptors()) {
              if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                userEntity.setProperty(pd.getName(), pd.getReadMethod().invoke(newCourseUser));
              }
            }
            if (courseMembersList.contains(newCourseUser.getCourseUserID())) {
              DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
              datastore.put(userEntity);
            }
            newCourseUser = new CourseUser();
            userEntity = new Entity("User", userKey);
          }

//          Drive.Files.List listRequest = service.files().list().setQ(
//                  "mimeType != 'application/vnd.google-apps.folder' "
//                  + "and sharedWithMe = true "
//                  + "and trashed = false"
//                  + "and title = '" + selectedFile + "'");
//          FileList filesList = listRequest.execute();
//          List<File> files = new ArrayList<>();
//          files.addAll(filesList.getItems());
//          Property newProperty = new Property();
//
//          newProperty.setKey("Parsed");
//          newProperty.setValue("true");
//          newProperty.setVisibility("PRIVATE");
//
//          for (int i = 0; i < files.size(); i++) {
//            service.properties().insert(files.get(i).getId(), newProperty).execute();
//          }
          session.setAttribute("infoString", "Users imported!");
        } else {
          session.setAttribute("infoString", "Problem in importing users!");
        }
      } catch (ParserConfigurationException |
               SAXException |
               IOException |
               IntrospectionException |
               IllegalAccessException |
               ClassCastException |
               InvocationTargetException ex) {
        ex.printStackTrace(System.out);
        throw new ServletException(ex);
      }
    }
    session.removeAttribute("sharedFiles");
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

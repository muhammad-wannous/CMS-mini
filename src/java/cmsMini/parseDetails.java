package cmsMini;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class parseDetails extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws java.io.IOException
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    ServletContext application = this.getServletContext();
    HttpSession session = request.getSession(true);
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
    CourseDetails thisCourseDetails = null;
    Course thisCourse = (Course) application.getAttribute("courseInfo");
    User thisUser = (User) session.getAttribute("userIn");
    /*The name of the xml file containing the course details is saved in the courseInfo.*/
    if ((thisCourse != null) && (thisUser != null)
            && (thisUser.getUserRole().endsWith("dmin"))) {
      String applicationID = application.getInitParameter("APPLICATION_ID");
      /*The xml file containing the course and instructor information is saved in a folder
       that is shared with us on Google Drive. The name of the folder is passed in web.xml*/
      String infoFolder = application.getInitParameter("INFO_FOLDER");
      try {
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationID).build();
        /*Get the information of the folder shared with us. Its name should be already known to us.*/
        Drive.Files.List listRequest = service.files().list().setQ(
                "mimeType != 'application/vnd.google-apps.file'"
                + "and sharedWithMe = true "
                + "and title = '" + thisCourse.getCourseDescriptionFile() + "'");
        FileList list = listRequest.execute();
        List<File> files = new ArrayList<>();
        /*This should return one file*/
        files.addAll(list.getItems());
        if (!files.isEmpty()
                && files.get(0).getDownloadUrl() != null
                && files.get(0).getDownloadUrl().length() > 0) {
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          dbFactory.setValidating(true);

          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          DTDEntityResolver dtdEntityResolver = new DTDEntityResolver();
          dtdEntityResolver.setApplication(application);
          dBuilder.setEntityResolver(dtdEntityResolver);
          int i; //counter
          Document detailsDocument = dBuilder.parse(service.getRequestFactory().buildGetRequest(
                  new GenericUrl(files.get(0).getDownloadUrl())).execute().getContent());
          NodeList details = detailsDocument.getElementsByTagName("course");
          if (details.getLength() > 0) {
            thisCourseDetails = new CourseDetails();
            Node courseDetailsNode = details.item(0);
            Node tempNode, inner1TempNode, inner2TempNode;
            tempNode = courseDetailsNode.getFirstChild();
            while (tempNode != null) {
              switch (tempNode.getNodeName()) {
                case "sourcedid":
                  inner1TempNode = tempNode.getFirstChild();
                  while (inner1TempNode != null) {
                    if (inner1TempNode.getNodeName().equals("id")) {
                      thisCourseDetails.setCourseID(inner1TempNode.getTextContent().trim());
                      break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  break;
                case "instructor":
                  thisCourseDetails.setInstructor(tempNode.getTextContent().trim());
                  break;
                case "semester":
                  thisCourseDetails.setSemester(tempNode.getTextContent().trim());
                  break;
                case "description":
                  inner1TempNode = tempNode.getFirstChild();
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "short":
                        thisCourseDetails.setCourseName(inner1TempNode.getTextContent().trim());
                        break;
                      case "long":
                        thisCourseDetails.setCatalogDescription(inner1TempNode.getTextContent().trim());
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  break;
                case "org":
                  inner1TempNode = tempNode.getFirstChild();
                  String departmentString = "";
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "orgname":
                        departmentString = inner1TempNode.getTextContent().trim() + "-";
                        break;
                      case "orgunit":
                        departmentString += (inner1TempNode.getTextContent().trim() + "-");
                        break;
                      case "type":
                        departmentString += (inner1TempNode.getTextContent().trim() + "-");
                        break;
                      case "id":
                        departmentString += inner1TempNode.getTextContent().trim();
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setDepartment(departmentString);
                  break;
                case "url":
                  thisCourseDetails.setUrl(tempNode.getTextContent().trim());
                  break;
                case "credits":
                  thisCourseDetails.setCredits(tempNode.getTextContent().trim());
                  break;
                case "level":
                  thisCourseDetails.setLevel(tempNode.getTextContent().trim());
                  break;
                case "schedule":
                  inner1TempNode = tempNode.getFirstChild();
                  String scheduleString = "";
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "daytime":
                        inner2TempNode = inner1TempNode.getFirstChild();
                        String oneDayString = "";
                        while (inner2TempNode != null) {
                          switch (inner2TempNode.getNodeName()) {
                            case "day":
                              oneDayString = "(" + inner2TempNode.getTextContent().trim() + ") ";
                              break;
                            case "fromtime":
                              oneDayString += (inner2TempNode.getTextContent().trim() + "~");
                              break;
                            case "totime":
                              oneDayString += inner2TempNode.getTextContent().trim();
                              break;
                            default:
                              break;
                          }
                          inner2TempNode = inner2TempNode.getNextSibling();
                        }
                        scheduleString += oneDayString + " ";
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setSchedule(scheduleString.trim());
                  break;
                case "prerequisites":
                  thisCourseDetails.setPrerequisites(tempNode.getTextContent().trim());
                  break;
                case "textbook":
                  inner1TempNode = tempNode.getFirstChild();
                  String textbookString = "";
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "author":
                        textbookString = inner1TempNode.getTextContent().trim() + ", ";
                        break;
                      case "title":
                        textbookString += ("\"" + inner1TempNode.getTextContent().trim() + "\" ");
                        break;
                      case "edition":
                        textbookString += (inner1TempNode.getTextContent().trim() + ".ed ");
                        break;
                      case "isbn":
                        textbookString += ("ISBN:" + inner1TempNode.getTextContent().trim());
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setTextBook(textbookString);
                  break;
                case "grading":
                  inner1TempNode = tempNode.getFirstChild();//<grade>
                  String[][] gradingsStringses = {{"", ""}, {"", ""}, {"", ""}};
                  i = 0;
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "grade":
                        gradingsStringses[i][0] = inner1TempNode.getAttributes().
                                getNamedItem("percentage").getTextContent().trim();
                        gradingsStringses[i++][1] = inner1TempNode.getTextContent().trim();
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setGrading(gradingsStringses);
                  break;
                case "outcomes":
                  inner1TempNode = tempNode.getFirstChild();//<outcome>
                  String[] outcomeStringses = {"", "", "", "", "", "", "", "", "", ""};
                  i = 0;
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "outcome":
                        outcomeStringses[i++] = inner1TempNode.getTextContent().trim();
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setObjectives(outcomeStringses);
                  break;
                case "topics":
                  inner1TempNode = tempNode.getFirstChild();//<grade>
                  String[][] topicStringses = {{"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""},
                  {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""}, {"", ""},
                  {"", ""}, {"", ""}, {"", ""}, {"", ""}};
                  i = 0;
                  while (inner1TempNode != null) {
                    switch (inner1TempNode.getNodeName()) {
                      case "topic":
                        topicStringses[i][0] = inner1TempNode.getAttributes().
                                getNamedItem("chapters").getTextContent().trim();
                        topicStringses[i++][1] = inner1TempNode.getTextContent().trim();
                        break;
                      default:
                        break;
                    }
                    inner1TempNode = inner1TempNode.getNextSibling();
                  }
                  thisCourseDetails.setToc(topicStringses);
                  break;
                default:
                  break;
              }
              tempNode = tempNode.getNextSibling();
            }
          }
        }
      } catch (ParserConfigurationException | SAXException | IOException ex) {
        System.out.println("\nError with DTD | xml.");
        ex.printStackTrace(System.out);
      }
    }
    if (thisCourseDetails != null) {
      application.setAttribute("courseDetails", thisCourseDetails);
      session.setAttribute("infoString", "Details parsed!");
    } else {
      session.setAttribute("infoString", "Details not parsed successfulyÏ!");
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

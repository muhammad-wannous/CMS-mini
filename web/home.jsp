<%-- 
    Document   : home
    Created on : Apr 1, 2014, 2:37:52 PM
    Author     : Muhammad Wannous
    Description: This is the main user interface of CMS-mini. Through this interface users login
                 find contents, upload files...etc. When loaded, this page searches for the course
                 information in the application context, if not found then fetch it from the data
                 store or from Google Drive.
--%>

<%@page import="com.google.api.services.drive.model.ParentReference"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" language="java"%>
<%@ page import="cmsMini.Course" %>
<%@ page import="cmsMini.User" %>
<%@ page import="org.w3c.dom.Node" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="cmsMini.DTDEntityResolver" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="org.xml.sax.InputSource" %>
<%@ page import="org.xml.sax.EntityResolver" %>
<%@ page import="javax.xml.parsers.ParserConfigurationException" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.IOException" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.api.client.googleapis.auth.oauth2.GoogleCredential" %>
<%@ page import="com.google.api.client.http.HttpTransport" %>
<%@ page import="com.google.api.client.http.javanet.NetHttpTransport" %>
<%@ page import="com.google.api.client.json.jackson2.JacksonFactory" %>
<%@ page import="com.google.api.services.drive.Drive" %>
<%@ page import="com.google.api.services.drive.DriveScopes" %>
<%@ page import="com.google.api.services.drive.model.About" %>
<%@ page import="com.google.api.services.drive.model.FileList" %>
<%@ page import="com.google.api.services.drive.model.File" %>
<%@ page import="com.google.api.client.http.GenericUrl" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.security.GeneralSecurityException" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ page import="cmsMini.CourseDetails" %>


<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>CMSmini homepage</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <link rel="icon" href="favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="css/jquery.mobile-1.4.2.min.css" />
        <script src="js/libs/jquery/jquery-2.1.1.min.js"></script>
        <script src="js/libs/jquery-mobile/jquery.mobile-1.4.2.min.js"></script>
    </head>
    <body class="scrollDiv">
        <div data-role="page">
            <%@include file="template/home.html" %>
            <script>
                var tempUser = document.getElementById("inUserID");
                var tempCourse = document.getElementById("courseTitle");
                var tempInfo = document.getElementById("infoBar");
                var resourcesTabContents = document.getElementById("contentsTab");
                <%
                  Course thisCourse = (Course) application.getAttribute("courseInfo");
                  String contentsFolderName = application.getInitParameter("CONTENTS_FOLDER");
                  User thisUser = (User) session.getAttribute("userIn");
                  if (thisCourse != null) {
                    pageContext.setAttribute("course", thisCourse);
                %>

                tempCourse.innerHTML = "${fn:escapeXml(course.courseTitle)}";
                tempCourse = document.getElementById("courseName");
                tempCourse.innerHTML = "${fn:escapeXml(course.courseTitle)}";
                tempCourse = document.getElementById("courseID");
                tempCourse.innerHTML = "${fn:escapeXml(course.courseID)}";

                <%
                  }
                  if (thisUser != null) {
                    pageContext.setAttribute("userIn", thisUser);
                    int i = 1;
                %>
                tempUser.innerHTML = "Welcome! " + "${fn:escapeXml(userIn.userID)}";
                tempUser = document.getElementById("logLink");
                tempUser.setAttribute("href", "logout");
                tempUser.innerHTML = "logout";
                var tempMenuItem = document.getElementById("menuItem<%= i%>");
                <%
                  /*Check if the user is administrator and the course has not been initialized then
                   give a link to initialize.*/
                  if (thisUser.getUserRole().endsWith("dmin")) {
                    if (!thisCourse.isBasicInfoSaved()) {
                %>
                tempMenuItem.innerHTML = "Save the basic information of the course!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "saveBasics");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                <%
                  }
                  if (!thisCourse.isDetailsSaved()) {
                %>
                tempMenuItem.innerHTML = "Parse the detailed information of the course!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "parseDetails");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Save the detialed information of the course!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "saveDetails");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                } else {
                  /*If the details were saved then we can display more menu items (starting at 1)
                   - Add users
                   - Add contents
                   */
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Add users!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "importUsers.jsp");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Add contents!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "importContents.jsp");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Create folders for users!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "createFolders");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                  i++;
                %>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Enable credentials for Android App!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "enableAndroid");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                      i++;
                    }
                  }
                  String homeFolderId = (String) session.getAttribute("homeFolderId");
                  if (homeFolderId != null && !homeFolderId.equals("")) {
                    //The following menu-items will be displayed to all users.
%>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "Upload a file to my folder!<br>";
                linkElement = document.createElement("a");
                linkElementText = document.createTextNode("here!");
                linkElement.setAttribute("href", "uploadFile.jsp");
                linkElement.setAttribute("rel", "external");
                linkElement.setAttribute("data-ajax", "false");
                linkElement.appendChild(linkElementText);
                tempMenuItem.appendChild(linkElement);
                <%
                    i++;
                  }
                %>
                    <%--Add Calendar--%>
                tempMenuItem = document.getElementById("menuItem<%= i%>");
                tempMenuItem.innerHTML = "<iframe src=\"https://www.google.com/calendar/embed?showNav=0&amp;showPrint=0&amp;showTabs=0&amp;showCalendars=0&amp;height=240&amp;wkst=7&amp;bgcolor=%23FFFFFF&amp;src=a63ish4fkfi4de2j05qdi3mbu4%40group.calendar.google.com&amp;color=%23875509&amp;ctz=Asia%2FDamascus\" style=\" border:solid 1px #777 \" width=\"320\" height=\"240\" frameborder=\"0\" scrolling=\"no\"></iframe>";
                <%
                  i++;
                  CourseDetails detailsObject = (CourseDetails) application.getAttribute("courseDetails");
                  /*If we have course details in the application context then set the display.*/
                  if (detailsObject != null) {
                    pageContext.setAttribute("detailsObject", detailsObject);
                %>
                var courseDetail = document.getElementById("courseName");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.courseName)}";
                courseDetail = document.getElementById("courseID");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.courseID)}";
                courseDetail = document.getElementById("courseDep");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.department)}";
                courseDetail = document.getElementById("courseInstructor");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.instructor)}";
                courseDetail = document.getElementById("courseSemester");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.semester)}";
                courseDetail = document.getElementById("courseCredits");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.credits)}";
                courseDetail = document.getElementById("courseLevel");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.level)}";
                courseDetail = document.getElementById("courseSche");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.schedule)}";
                courseDetail = document.getElementById("courseURL");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.url)}";
                courseDetail = document.getElementById("coursePre");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.prerequisites)}";
                courseDetail = document.getElementById("courseText");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.textBook)}";
                courseDetail = document.getElementById("grade1");
                courseDetail.innerHTML =
                <%= "\"" + detailsObject.getGrading()[0][0]
                        + " : "
                        + detailsObject.getGrading()[0][1]
                        + "\""%>;
                courseDetail = document.getElementById("grade2");
                courseDetail.innerHTML =
                <%= "\"" + detailsObject.getGrading()[1][0]
                        + " : "
                        + detailsObject.getGrading()[1][1]
                        + "\""%>;
                courseDetail = document.getElementById("grade3");
                courseDetail.innerHTML =
                <%= "\"" + detailsObject.getGrading()[2][0]
                        + " : "
                        + detailsObject.getGrading()[2][1]
                        + "\""%>;
                courseDetail = document.getElementById("catalogueDes");
                courseDetail.innerHTML = "${fn:escapeXml(detailsObject.catalogDescription)}";
                <%
                  for (i = 1; i < 11; i++) {
                %>
                courseDetail = document.getElementById("outCome<%=i%>");
                courseDetail.innerHTML = <%= "\"" + detailsObject.getObjectives()[i - 1] + "\""%>;
                <%
                  }
                  for (i = 1; i < 21; i++) {
                %>
                courseDetail = document.getElementById("chap<%= i%>");
                courseDetail.innerHTML = <%= "\"" + detailsObject.getToc()[i - 1][0] + "\""%>;
                courseDetail = document.getElementById("topic<%= i%>");
                courseDetail.innerHTML = <%= "\"" + detailsObject.getToc()[i - 1][1] + "\""%>;
                <%
                  }
                  Map<String, String[]> filesInfo = new HashMap<>();
                  session.removeAttribute("filesInfo");
                  List<ParentReference> parentsList;
                  /*Get folders first. Put them in collapsible*/
                  HttpTransport httpTransport = new NetHttpTransport();
                  JacksonFactory jsonFactory = new JacksonFactory();
                  GoogleCredential credential = (GoogleCredential) application.getAttribute("credential");
                  String applicationID = application.getInitParameter("APPLICATION_ID");
                  if (credential != null && applicationID != null && !applicationID.equals("")) {
                    Drive serviceDrive = new Drive.Builder(httpTransport, jsonFactory, credential)
                            .setApplicationName(applicationID).build();
                    Drive.Files.List listFoldersRequest = serviceDrive.files().list().setQ("trashed = false "
                            + "and mimeType = 'application/vnd.google-apps.folder'");
                    FileList foldersList = listFoldersRequest.execute();
                    List<File> folders = new ArrayList<>();
                    folders.addAll(foldersList.getItems());
                    if (!folders.isEmpty()) {
                      for (File folder : folders) {
                        boolean displayB = false;
                        //For admin users, show all folders including the shared with us.
                        //For other users show only the contents folder and their ownes.
                        if (!thisUser.getUserRole().endsWith("dmin")) {
                          //Not admin
                          parentsList = folder.getParents();
                          if (folder.getSharedWithMeDate() == null) {
                            for (ParentReference parentReference : parentsList) {
                              if (parentReference.getIsRoot()
                                      && (folder.getTitle().equals(thisUser.getUserID())
                                      || folder.getTitle().equals(contentsFolderName))) {
                                displayB = true;
                                break;
                              }
                            }
                          }
                        } else {
                          //Admin, check if the folder is root.
                          parentsList = folder.getParents();
                          for (ParentReference parentReference : parentsList) {
                            if (parentReference.getIsRoot()) {
                              displayB = true;
                              break;
                            }
                          }
                        }
                        if (displayB) {
                %>
                resourcesTabContents.innerHTML = resourcesTabContents.innerHTML + '<fieldset data-role="collapsible">'
                        + '<legend><%=folder.getTitle()%></legend>'
                        + '<p>Sub folders and files.</p>'
                        + <%=getFolderContents(0, folder.getId(), serviceDrive, filesInfo)%>
                + '</fieldset>';
                <%
                      }
                    }
                  }
                  /*Next bring file. Put them in collapsible*/
                  Drive.Files.List listFilesRequest = serviceDrive.files().list().setQ("trashed = false "
                          + "and mimeType != 'application/vnd.google-apps.folder'");
                  FileList filesList = listFilesRequest.execute();
                  List<File> files = new ArrayList<>();
                  files.addAll(filesList.getItems());
                  if (!files.isEmpty()) {
                %>
                resourcesTabContents.innerHTML = resourcesTabContents.innerHTML
                        + '<ul data-role="listview" data-inset="true">';
                var j = 0;
                <%
                  for (File file : files) {
                    parentsList = file.getParents();
                    boolean displayB = false;
                    if (file.getSharedWithMeDate() == null) {
                      if (parentsList == null) {
                        displayB = true;
                      } else {
                        for (ParentReference parentReference : parentsList) {
                          if (parentReference.getIsRoot()) {
                            displayB = true;
                          }
                        }
                      }
                    }
                    if (displayB) {
                      String[] fileInfoStrings = new String[3];
                      fileInfoStrings[0] = file.getTitle();
                      fileInfoStrings[1] = "";
                      if (file.getMimeType().endsWith("apps.document")
                              || file.getMimeType().endsWith("apps.drawing")
                              || file.getMimeType().endsWith("apps.form")
                              || file.getMimeType().endsWith("apps.presentation")
                              || file.getMimeType().endsWith("apps.spreadsheet")) {
                        fileInfoStrings[0] = file.getTitle() + " (download in PDF format)";
                        fileInfoStrings[2] = file.getExportLinks().get("application/pdf");
                      } else {
                        fileInfoStrings[2] = file.getDownloadUrl();
                      }
                      filesInfo.put(file.getId(), fileInfoStrings);
                %>
                resourcesTabContents.innerHTML = resourcesTabContents.innerHTML
                        + '<li><a id="' + j++ + '"'
                        + 'target="_blank" href="download?id=<%=file.getId()%>"><%=filesInfo.get(file.getId())[0]%>'
                        + '</a></li>';
                <%
                    }
                  }
                %>
                resourcesTabContents.innerHTML = resourcesTabContents.innerHTML + '</ul>';
                <%
                      }
                      session.setAttribute("filesInfo", filesInfo);
                    }
                  }
                } else {
                %>
                tempUser.innerHTML = "Log in";
                tempUser = document.getElementById("logLink");
                tempUser.setAttribute("href", "login.jsp");
                tempUser.innerHTML = "here!";
                <%
                  }
                  String infoString = (String) session.getAttribute("infoString");
                  if (infoString != null) {
                    pageContext.setAttribute("infoString", infoString);
                %>
                tempInfo.innerHTML = "${fn:escapeXml(infoString)}";
                <%
                  }
                %>
            </script>
        </div>
    </body>
</html>

<%!
  /*The initialization routine.
   Try to create a new  access credentials and save it into the application context.*/
  /*The credential is used for accessing Google Drive API from our App Engine project.*/
  public void jspInit() {
    ServletContext application = this.getServletContext();
    String applicationID = application.getInitParameter("APPLICATION_ID");
    /*The xml file containing the course and instructor information is saved in a folder
     that is shared with us on Google Drive. The name of the folder is passed in web.xml*/
    String infoFolder = application.getInitParameter("INFO_FOLDER");
    /*The credential needs HttpTransport, JacksonFactory, account ID, and KeyFile.*/
    HttpTransport httpTransport = new NetHttpTransport();
    JacksonFactory jsonFactory = new JacksonFactory();
    Course thisCourse = null;
    User adminUser = null;
    GoogleCredential credential = null;
    /*we need credential, courseInfo, and adminUser in the context.*/
    boolean contextReady = false;
    credential = (GoogleCredential) application.getAttribute("credential");
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
        ex.printStackTrace(System.out);
        return;
      }
    }
    //Credential necessary for accessing Google Drive API has already been set.
    thisCourse = (Course) application.getAttribute("courseInfo");
    adminUser = (User) application.getAttribute("adminInfo");
    if ((thisCourse != null) && (adminUser != null)) {
      /*Course information has not already been parsed from xml file, or jsp restarting.*/
      contextReady = true;
    }
    if (!contextReady) {
      /*Check weather we are restarting. In this case the course and admin entities should not be empty.*/
      /*First, make sure that nothing is saved in the context.*/
      application.removeAttribute("courseInfo");
      application.removeAttribute("adminInfo");
      /*Make sure nothing left (even if one has been set, we need the two).*/
      /*Check the datastore.*/
      /*We have three tables identified by the string named tableName.
       One table is for courses, one for course admins, and one for users*/
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key courseKey = KeyFactory.createKey("tableName", "Course");
      Key adminUserKey = KeyFactory.createKey("tableName", "Admin");
      Query courseQuery = new Query("Course", courseKey);//Should return one itme.
      Query adminUserQuery = new Query("Admin", adminUserKey); //Should return one item.
      List<Entity> dbCourses = datastore.prepare(courseQuery).asList(FetchOptions.Builder.withDefaults());
      List<Entity> dbAdmins = datastore.prepare(adminUserQuery).asList(FetchOptions.Builder.withDefaults());

      if (!dbCourses.isEmpty() && !dbAdmins.isEmpty()) {//xml file has been parsed.
        Entity dbCourse = dbCourses.get(0);
        Entity dbAdmin = dbAdmins.get(0);
        thisCourse = new Course((String) dbCourse.getProperty("courseID"),
                (String) dbCourse.getProperty("courseTitle"),
                (String) dbCourse.getProperty("courseDescriptionFile"),
                (boolean) dbCourse.getProperty("basicInfoSaved"),
                (boolean) dbCourse.getProperty("detailsSaved"));
        adminUser = new User((String) dbAdmin.getProperty("userID"),
                (String) dbAdmin.getProperty("userMD5Pass"),
                (String) dbAdmin.getProperty("userRole"));
        application.setAttribute("adminInfo", adminUser);
        application.setAttribute("courseInfo", thisCourse);
        contextReady = true;
      }

      /*If we could not find entries in the datastore then make sure that the datastre is empty.*/
      if (!contextReady && !dbCourses.isEmpty()) {
        while (!dbCourses.isEmpty()) {
          datastore.delete(dbCourses.remove(0).getKey());
        }
      }
      if (!contextReady && !dbAdmins.isEmpty()) {
        while (!dbAdmins.isEmpty()) {
          datastore.delete(dbAdmins.remove(0).getKey());
        }
      }
    }/*Try parsing the xml file*/

    if (!contextReady) {
      try {
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationID).build();
        /*Get the information of the folder shared with us. Its name should be already known to us.*/
        Drive.Files.List listRequest = service.files().list().setQ(
                "mimeType = 'application/vnd.google-apps.folder' "
                + "and sharedWithMe = true "
                + "and title = '" + infoFolder + "'");
        /*This should return one folder.*/
        FileList list = listRequest.execute();
        List<File> folders = new ArrayList<>();
        List<File> files = new ArrayList<>();
        folders.addAll(list.getItems());
        if (!folders.isEmpty()) {
          /*List the files in the folder and search for courseInfo.xml*/
          listRequest = service.files().list().setQ("'" + folders.remove(0).getId() + "' in parents "
                  + "and title = 'courseInfo.xml'"
                  + "and mimeType != 'application/vnd.google-apps.folder'");
          list = listRequest.execute();
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
            Document infoDocument = dBuilder.parse(service.getRequestFactory().buildGetRequest(
                    new GenericUrl(files.get(0).getDownloadUrl())).execute().getContent());
            NodeList users = infoDocument.getElementsByTagName("person");
            if (users.getLength() > 0) { //At least one user is defined in courseInfo.xml file.
          /*We need to obtain the user ID, password, and role.*/
              Node userNode = users.item(0);
              Node userIDNode = null;
              Node userRoleNode = null;
              Node tempNode = null;
              tempNode = userNode.getFirstChild();
              /*We will need 2 nodes only <userid> <systemrole>*/
              while (tempNode != null) {
                switch (tempNode.getNodeName()) {
                  case "userid":
                    userIDNode = tempNode;
                    break;
                  case "systemrole":
                    if ((tempNode.getAttributes()).getNamedItem("systemroletype")
                            .getTextContent().endsWith("dmin")) {
                      userRoleNode = tempNode;
                    }
                    break;
                  default:
                    break;
                }
                tempNode = tempNode.getNextSibling();
              }
              if ((userIDNode != null) && (userRoleNode != null)) {
                adminUser = new User(userIDNode.getTextContent().trim(),
                        userIDNode.getAttributes().getNamedItem("password").getTextContent().trim(),
                        "Admin".trim());
                System.out.println(adminUser.getUserID() + " : "
                        + adminUser.getUserMD5Pass() + " : " + adminUser.getUserRole());
              }
            }
            NodeList courses = infoDocument.getElementsByTagName("group");
            if (courses.getLength() > 0) { //At least one course is defined in courseInfo.xml file.
          /*We need to obtain the course ID, Name, and definition file.*/
              Node courseNode = courses.item(0);
              Node courseIDNode = null;
              Node courseTitleNode = null;
              Node courseDescriptionFileNode = null;
              Node tempNode = null, tempNode1 = null;
              tempNode = courseNode.getFirstChild();
              /*We will need 3 nodes only <sourcedid><id> <description><short> <extension>*/
              while (tempNode != null) {
                switch (tempNode.getNodeName()) {
                  case "sourcedid":
                    tempNode1 = tempNode.getFirstChild();
                    while (tempNode1 != null) {
                      if (tempNode1.getNodeName().equals("id")) {
                        courseIDNode = tempNode1;
                        break;
                      }
                      tempNode1 = tempNode1.getNextSibling();
                    }
                    break;
                  case "description":
                    tempNode1 = tempNode.getFirstChild();
                    while (tempNode1 != null) {
                      if (tempNode1.getNodeName().equals("short")) {
                        courseTitleNode = tempNode1;
                        break;
                      }
                      tempNode1 = tempNode1.getNextSibling();
                    }
                    break;
                  case "extension":
                    courseDescriptionFileNode = tempNode;
                    break;
                  default:
                    break;
                }
                tempNode = tempNode.getNextSibling();
              }
              if ((courseIDNode != null) && (courseTitleNode != null) && (courseDescriptionFileNode != null)) {
                thisCourse = new Course(courseIDNode.getTextContent().trim(),
                        courseTitleNode.getTextContent().trim(),
                        courseDescriptionFileNode.getTextContent().trim(), false, false);
                System.out.println(thisCourse.getCourseID() + " : " + thisCourse.getCourseTitle() + " : "
                        + thisCourse.getCourseDescriptionFile());
              }
            }
            application.setAttribute("adminInfo", adminUser);
            application.setAttribute("courseInfo", thisCourse);
            contextReady = true;
          }
        }
      } catch (ParserConfigurationException | SAXException | IOException ex) {
        System.out.println("\nError with DTD | xml.");
        ex.printStackTrace(System.out);
      }
    }

    CourseDetails courseDetails = (CourseDetails) application.getAttribute("courseDetails");
    if (contextReady && (thisCourse.isDetailsSaved()) && (courseDetails == null)) {
      /*The details were saved in the Datastore but they are not in the context. Get them from
       the Datastore*/
      /*We have three tables identified by the string named tableName.
       One table is for courses, one for course admins, and one for users*/
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key detailsKey = KeyFactory.createKey("tableName", "Details");
      Query detailsQuery = new Query("Details", detailsKey);
      List<Entity> dbDetails = datastore.prepare(detailsQuery).asList(FetchOptions.Builder.withDefaults());
      if (!dbDetails.isEmpty()) {
        Entity courseDetialsEntity = dbDetails.get(0);
        courseDetails = new CourseDetails();
        courseDetails.setCourseID((String) courseDetialsEntity.getProperty("courseID"));
        courseDetails.setCourseName((String) courseDetialsEntity.getProperty("courseName"));
        courseDetails.setCredits((String) courseDetialsEntity.getProperty("credits"));
        courseDetails.setDepartment((String) courseDetialsEntity.getProperty("department"));
        int i;
        String[][] tempStr = new String[3][2];
        String[] tempStr1 = new String[10];
        for (i = 0; i < 3; i++) {
          tempStr[i][0] = (String) courseDetialsEntity.getProperty("grading[" + i + "][0]");
          tempStr[i][1] = (String) courseDetialsEntity.getProperty("grading[" + i + "][1]");
        }
        courseDetails.setGrading(tempStr);
        courseDetails.setInstructor((String) courseDetialsEntity.getProperty("instructor"));
        courseDetails.setLevel((String) courseDetialsEntity.getProperty("level"));
        for (i = 0; i < 10; i++) {
          tempStr1[i] = (String) courseDetialsEntity.getProperty("objectives[" + i + "]");
        }
        courseDetails.setObjectives(tempStr1);
        courseDetails.setPrerequisites((String) courseDetialsEntity.getProperty("prerequisites"));
        courseDetails.setSchedule((String) courseDetialsEntity.getProperty("schedule"));
        courseDetails.setSemester((String) courseDetialsEntity.getProperty("semester"));
        courseDetails.setCatalogDescription((String) courseDetialsEntity.getProperty("catalogDescription"));
        courseDetails.setTextBook((String) courseDetialsEntity.getProperty("textBook"));
        tempStr = new String[20][2];
        for (i = 0; i < 20; i++) {
          tempStr[i][0] = (String) courseDetialsEntity.getProperty("toc[" + i + "][0]");
          tempStr[i][1] = (String) courseDetialsEntity.getProperty("toc[" + i + "][1]");
        }
        courseDetails.setToc(tempStr);
        courseDetails.setUrl((String) courseDetialsEntity.getProperty("url"));
        application.setAttribute("courseDetails", courseDetails);
      }
    }
    /*A list of users who are currently logged in*/
    ArrayList<String> usersInList = new ArrayList<>();
    application.setAttribute("usersInList", usersInList);
  }
%>
<%!
  public String getFolderContents(int i, String parentIdString, Drive service, Map<String, String[]> filesInfo) {
    //String resultString = "<div data-role=\"controlgroup\">\n";
    String resultString = "'<div class=\"ui-content\">'\n";
    try {
      Drive.Files.List listSubFoldersRequest = service.files().list().setQ("trashed = false "
              + "and mimeType = 'application/vnd.google-apps.folder' "
              + "and '" + parentIdString + "' in parents");
      FileList subFoldersList = listSubFoldersRequest.execute();
      List<File> subFolders = new ArrayList<>();
      subFolders.addAll(subFoldersList.getItems());
      if (!subFolders.isEmpty()) {
        for (File subFolder : subFolders) {
          resultString = resultString + " + '<fieldset data-role=\"collapsible\">'\n"
                  + "+ '<legend>" + subFolder.getTitle() + "</legend>'\n"
                  + "+ '<p>Sub folders and files.</p>'\n"
                  + "+ " + getFolderContents(i + 1, subFolder.getId(), service, filesInfo)
                  + "+ '</fieldset>'\n";
        }
      }
      Drive.Files.List listChildFilesRequest = service.files().list().setQ("trashed = false "
              + "and mimeType != 'application/vnd.google-apps.folder' "
              + "and '" + parentIdString + "' in parents");
      FileList chilFileList = listChildFilesRequest.execute();
      List<File> childFiles = new ArrayList<>();
      childFiles.addAll(chilFileList.getItems());
      if (!childFiles.isEmpty()) {
        resultString = resultString + "+ '<ul data-role=\"listview\" data-inset=\"true\">'\n";
        int j = 1;
        for (File childFile : childFiles) {
          String[] fileInfoStrings = new String[3];
          fileInfoStrings[0] = childFile.getTitle();
          fileInfoStrings[1] = parentIdString;
          if (childFile.getMimeType().endsWith("apps.document")
                  || childFile.getMimeType().endsWith("apps.drawing")
                  || childFile.getMimeType().endsWith("apps.form")
                  || childFile.getMimeType().endsWith("apps.presentation")
                  || childFile.getMimeType().endsWith("apps.spreadsheet")) {
            fileInfoStrings[0] = childFile.getTitle() + " (download in PDF format)";
            fileInfoStrings[2] = childFile.getExportLinks().get("application/pdf");
          } else {
            fileInfoStrings[2] = childFile.getDownloadUrl();
          }
          filesInfo.put(childFile.getId(), fileInfoStrings);
          resultString = resultString
                  //+ "<label for=\"" + i + "x" + j + "\">" + childFile.getTitle() + "</label>\n"
                  //                  + "<input type=\"checkbox\" name=\"favcolor\" id=\"" + i + "x" + j++ + "\" value=\""
                  //                  + childFile.getTitle() + "\">\n"
                  + " + '<li>'\n"
                  + " + '<a id=\"" + i + "x" + j++ + "\" target=\"_blank\" "
                  + "href=\"download?id=" + childFile.getId() + "\">" + filesInfo.get(childFile.getId())[0]
                  + "</a>'"
                  + "+ '</li>'\n";
        }
        resultString = resultString + "+ '</ul>'\n";
      }
    } catch (IOException ex) {
      System.out.println("Error in explorer.jsp#getFolderContents!");
      ex.printStackTrace(System.out);
    }
    resultString = resultString + "+ '</div>'\n";
    return resultString;
  }
%>
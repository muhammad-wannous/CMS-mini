<%-- 
    Document   : importContents.jsp
    Created on : Mar 13, 2014, 4:44:27 PM
    Author     : Muhammad Wannous
--%>

<%@ page import="com.google.api.services.drive.model.Property" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.api.services.drive.model.File" %>
<%@ page import="com.google.api.services.drive.model.FileList" %>
<%@ page import="com.google.api.services.drive.Drive" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="cmsMini.User" %>
<%@ page import="cmsMini.Course" %>
<%@ page import="cmsMini.CourseDetails" %>
<%@ page import="java.security.GeneralSecurityException" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.google.api.services.drive.DriveScopes" %>
<%@ page import="com.google.api.client.googleapis.auth.oauth2.GoogleCredential" %>
<%@ page import="com.google.api.client.json.jackson2.JacksonFactory" %>
<%@ page import="com.google.api.client.http.javanet.NetHttpTransport" %>
<%@ page import="com.google.api.client.http.HttpTransport" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Select a file to import contents from!</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <link rel="icon" href="favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="css/jquery.mobile-1.4.2.min.css" />
        <script src="js/libs/jquery/jquery-2.1.1.min.js"></script>
        <script src="js/libs/jquery-mobile/jquery.mobile-1.4.2.min.js"></script>
    </head>
    <body>
        <div data-role="page" data-ajax="false">
            <div id="logoArea">
                <table data-role="none" class="noBorder">
                    <tr>
                        <td class="solidRoundEdges">
                            <img src="images/Logo.png" alt="Shin-logo" height="96" width="160"></td>
                        <td class="hiddenRightAlignment"></td>
                    </tr>
                </table>
            </div>
            <hr class="wide">
            <%
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
                }
              }
              /*This Servlet will give the admin user a list of the xml files in the shared Google Drive to select
               one from them to import the user information from.*/
              CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
              Course thisCourse = (Course) application.getAttribute("courseInfo");
              User thisUser = (User) session.getAttribute("userIn");
              if (((credential == null) || (thisCourseDetails == null) || (thisCourse == null)
                      || (thisUser == null)) || (!thisUser.getUserRole().endsWith("dmin"))) {
                System.out.println("\nError in importContents.");
                response.sendRedirect("home.jsp");
              } else {
                /*Prepare a list of the files in the shared Google Drive*/
                Map<String, String> sharedFiles = new HashMap<>();
                Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(applicationID).build();
                /*Get the information of the folder shared with us.*/
                Drive.Files.List listRequest = service.files().list().setQ(
                        "mimeType != 'application/vnd.google-apps.folder' "
                        + "and sharedWithMe "
                        + "and trashed = false");

                FileList fileList = listRequest.execute();
                List<File> files = new ArrayList<>();
                files.addAll(fileList.getItems());
                if (!files.isEmpty()) {
                  /*Add all files to the Map.*/
                  for (File file : files) {
                    if (file.getTitle().endsWith(".zip")
                            && file.getDownloadUrl() != null
                            && file.getDownloadUrl().length() > 0) {
                      sharedFiles.put(file.getTitle(), file.getDownloadUrl());
                    }
                  }
                  session.setAttribute("sharedContentFiles", sharedFiles);
                }
            %>
            <h2 class="Helvetica">Please select a file from the list below to import users from.</h2>
            <form  action="addContents" id="addContentsForm" method="POST" >
                <select data-role="none" id="filesList" name="selectedContentFile">

                    <%
                      for (String sharedFileName : sharedFiles.keySet()) {
                    %>
                    <option data-role="none" value="<%=sharedFileName%>"><%=sharedFileName%></option>
                    <%
                      }
                    %>
                </select>
                <input data-role="none" type="submit" value="Submit"/>
            </form>
            <%
              }
            %>
        </div>
    </body>
</html>

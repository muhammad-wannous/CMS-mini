<%-- 
    Document   : uploadFile
    Created on : Jun 13, 2014, 4:44:27 PM
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
              CourseDetails thisCourseDetails = (CourseDetails) application.getAttribute("courseDetails");
              Course thisCourse = (Course) application.getAttribute("courseInfo");
              User thisUser = (User) session.getAttribute("userIn");
              String homeFolderId = (String) session.getAttribute("homeFolderId");
              if ((thisCourseDetails == null) || (thisCourse == null)
                      || (thisUser == null)
                      || (homeFolderId == null) || homeFolderId.equals("")) {
                System.out.println("uploadFile# No credential found in context or home folder!");
                session.setAttribute("infoString", "No credential!");
              } else {

            %>
            <h2 class="Helvetica">Please select a file to upload. (Only PDF files are allowed.)</h2>
            <form  action="saveFile" id="saveFileForm" method="POST" >
                <input type="file" name="fileToUpload" accept="application/pdf">
                <input type="submit" value="Upload">
            </form>
            <%              
              }
            %>
        </div>
    </body>
</html>

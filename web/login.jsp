<%-- 
    Document   : login
    Created on : Feb 10, 2014, 10:00:38 AM
    Author     : Muhammad WANNOUS
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CMSmini login page</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <link rel="icon" href="favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="css/jquery.mobile-1.4.2.min.css" />
        <script src="js/libs/jquery/jquery-2.1.1.min.js"></script>
        <script src="js/libs/jquery-mobile/jquery.mobile-1.4.2.min.js"></script>
        <script src="js/libs/CryptoJS-v3.1.2/md5.js"></script>
        <script>function submitForm()
            {
                var passInput = document.forms["loginForm"]["userSecretInput"].value;
                if (passInput !== null && passInput !== "") {
                    document.forms["loginForm"]["userSecretInput"].value = CryptoJS.MD5(passInput);
                    document.getElementById("loginForm").submit();
                }
            }
            function guestSubmit()
            {
                document.forms["loginForm"]["userIDInput"].value = "guest";
                document.forms["loginForm"]["userSecretInput"].value = CryptoJS.MD5("guest");
                document.getElementById("loginForm").submit();
            }
        </script>
    </head>
    <body>
        <div data-role="page">
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
            <form action="validate" id="loginForm" method="POST">
                <table data-role="none" class="noBorder">
                    <tr>
                        <td>ID:</td>
                        <td><input data-role="none" type="text" id="userIDInput" name="userID"></td>
                    </tr>
                    <tr>
                        <td>Secret:</td>
                        <td><input data-role="none" type="password" id="userSecretInput" name="userSecret"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><input data-role="none" type="button" value="Login" onclick="submitForm()" ></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><input data-role="none" type="button" value="Guest Login" onclick="guestSubmit()" ></td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>

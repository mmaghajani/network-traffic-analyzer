<%-- 
        *** In The Name of Allah ***
    Document   : index.jsp
    Created on : Jul 24, 2017, 3:45:03 PM
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="avserver.config.Config"%>
<%
    response.setHeader("Upload-Limit", Long.toString(Config.MAX_FILE_SIZE));
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Upload File</title>
        <base href="${pageContext.request.contextPath}/"/>
        <link rel="icon" type="image/png" href="favicon.png" />
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <script src="node_modules/jquery/dist/jquery.min.js"></script>
        <script src="js/SharedPreferences.js"></script>
        <script src="js/global.js"></script>
        <script src="js/index.js"></script>
    </head>

    <body>
        <div class="w3-content w3-container">
            <div>
                <h1 id="appName" class="w3-center w3-text-green"></h1>
            </div>

            <div id="body-container" class="w3-twothird">
                <div class="w3-bar">
                    <div class="w3-row">
                        <button id="tab-button-upload"
                                class="w3-bar-item w3-button w3-border-left w3-border-right w3-border-top w3-border-bottom"
                                onclick="openTab('Upload')">
                        </button>
                        <div class="w3-rest" id="garbage-container">
                            <div id="garbage" class="w3-border-bottom w3-container"></div>
                        </div>
                    </div>

                </div>

                <div class="w3-border-bottom w3-border-left w3-border-right w3-round-jumbo w3-border-gray"
                     id="content-container">
                    <div id="Upload" class="w3-container tab w3-center">
                        <i class="material-icons">fingerprint</i>
                        <form action="upload?lang=fa" method="post" class="w3-center" enctype="multipart/form-data">
                            <input id="fileToUpload" type="file" name="file">
                            <input id="submit-button"
                                   class="w3-center w3-button w3-white w3-border w3-border-green w3-text-green w3-round-medium l3 m3 s2 w3-mobile"
                                   value="" name="submit" readonly>
                            <img id="loading-icon" src="assets/30.gif" class="loading w3-center">
                            <input id="submit" type="submit" name="submit">
                            
                        </form>
                        <br>
                        <div id="text-container" class="w3-container">

                        </div>
                    </div>

                </div>
                <div id="error-panel" class="error-panel w3-panel w3-round-medium w3-pale-red w3-display-container">
                    <span id="error-close" onclick="this.parentElement.style.display = 'none'"
                          class="w3-button w3-round-medium w3-pale-red w3-large">&times;</span>
                    <h3 id="error-panel-header"></h3>
                    <ul id="error-list">

                    </ul>
                </div>
            </div>
        </div>
    </body>
</html>
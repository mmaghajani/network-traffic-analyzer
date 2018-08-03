<%-- 
        *** In The Name of Allah ***
    Document   : index.jsp
    Created on : Jul 24, 2017, 3:45:03 PM
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page isErrorPage="true"%>
<%@page import="avserver.utils.INet"%>
<%@page import="avserver.utils.Logger"%>
<%
    int httpStatus = response.getStatus();
    try {
        if(request.getAttribute("javax.servlet.error.status_code") != null)
            httpStatus = (Integer) request.getAttribute("javax.servlet.error.status_code");
    } catch (Exception ex) {
        Logger.error(ex);
    }
    Logger.info("ERROR.jsp :: RET #" + httpStatus + " for URL = " + 
				request.getAttribute("javax.servlet.forward.request_uri") + 
				"  |  IP = " + INet.getIP(request) +
				"  |  User-Agent = '" + request.getHeader("User-Agent"));
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Error</title>
        <base href="${pageContext.request.contextPath}/"/>
        <link rel="icon" type="image/png" href="favicon.png" />
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <script src="node_modules/jquery/dist/jquery.min.js"></script>
        <script src="js/SharedPreferences.js"></script>
        <script src="js/global.js"></script>
        <script src="js/error.js"></script>
    </head>
    <body>
        <input id="status-error-code" type="text" style="display: none" value="<%=httpStatus%>">
        <div class="w3-content w3-container">
            <div>
                <h1 id="appName" class="w3-center w3-text-green"></h1>
            </div>

            <div id="body-container" class="w3-twothird w3-center">
                <div id="error-code">
                </div>
                <div id="error-description">

                </div>
            </div>
        </div>
    </body>
</html>

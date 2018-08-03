<%--
	*** In The Name of Allah ***
    Document   : report.jsp
    Created on : Jul 18, 2017, 1:31:22 PM
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.ArrayList" %>
<%@page import="avserver.model.Report" %>
<%
	long fileSize = (Long) session.getAttribute("fileSize");
	String fileName = (String) session.getAttribute("fileName");
	String fileSizeStr = (String) session.getAttribute("fileSizeStr");
	String contentType = (String) session.getAttribute("contentType");
	String sha256Digest = (String) session.getAttribute("sha256Digest");
%>
<!DOCTYPE html>
<html>
	<head>
		<title>Scan Result</title>
		<base href="${pageContext.request.contextPath}/"/>
		<link rel="icon" type="image/png" href="favicon.png" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<script src="node_modules/jquery/dist/jquery.min.js"></script>
		<script src="js/SharedPreferences.js"></script>
		<script src="js/global.js"></script>
		<script src="js/report.js"></script>
		<style></style>
	</head>
	<body class="w3-row">

		<div class="w3-display-container content-panel">
			<div class="w3-col l10 m11 w3-display-topmiddle w3-mobile">
				<div class="w3-container w3-cell-row w3-responsive">
					<div class="l2 m2 w3-cell w3-cell-top">
						<img id="icon" alt="assets/exe-executable-file-format-interface-symbol.png" class="w3-image">
					</div>
					<div class="file-info l10 m10 w3-cell">
						<table class="w3-table w3-tiny w3-cell-row">
							<tr>
								<td id="fileNameTitle" class="report-title"></td>
								<td id="fileName"><%= fileName%></td>
							</tr>
							<tr>
								<td id="format" class="report-title"></td>
								<td id="type"><%= contentType%></td>
							</tr>
							<tr>
								<td id="size" class="report-title"></td>
								<td class="details"><%= fileSizeStr%></td>
								<td id="fileSize"><%= fileSize%></td>
							</tr>
							<tr class="SHA">
								<td class="report-title"> SHA-256</td>
								<td id="SHA256"><%= sha256Digest%></td>
							</tr>
							<tr>
								<td id="id" class="report-title"></td>
								<td id="scanID"> </td>
							</tr>
							<tr>
								<td id="originAddress" class="report-title"></td>
								<td id="scanIP"> </td>
							</tr>
							<tr>
								<td id="countryTitle" class="report-title"></td>
								<td id="country"> </td>
							</tr>
							<tr>
								<td id="timeTitle" class="report-title"></td>
								<td class="details" id="time"> </td>
							</tr>
						</table>
					</div>
				</div>

				<div id="oldReportBanner" class="w3-container old-report-banner">
					<div class="w3-container w3-border w3-round-small warning-panel">
						<p id="oldBannerContent" class="warning-panel-content">
							<%//= new java.sql.Date(reports.get(0).SCAN.TIME.getTime()).toString()%>
							<%//= new java.sql.Time(reports.get(0).SCAN.TIME.getTime()).toString()%>
						</p>
						<button type="button" id="rescan" onclick="doRescan()" class="w3-button w3-round-small rescan-btn w3-hover-shadow"></button>
					</div>
				</div>

				<div class="w3-container w3-responsive">
					<table id="result" class="w3-table w3-bordered w3-border w3-card-2">
						<tr>
							<th id="av" class="header-table"></th>
							<th id="detection" class="header-table"><span id="detection-header"></span></th>
							<th id="description" class="header-table"></th>
						</tr>
					</table>

				</div>
				<div class="w3-container w3-responsive">
					<a href="index.jsp">
						<button id="return" class="w3-button w3-round-small back-button w3-hover-shadow"></button>
					</a>
				</div>
			</div>
		</div>
		<div id="loading" class="scanning w3-display-middle">
			
		</div>

		<div class="page-cover">
		</div>
	</body>
</html>

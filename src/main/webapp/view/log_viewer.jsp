<%@ page import="java.io.File" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.util.stream.Stream" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Collections" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en" ng-app="SyncApp">
<head>
    <%@include file="common/resoucelink_head.jsp" %>

    <style>
        #page-content-wrapper {
            padding: 5px;
        }
        /* Specific styles for the Log Viewer to fit inside page-content-wrapper */
        .log-viewer-container {
            background-color: #1e1e1e; /* Dark background for code/logs */
            color: #d4d4d4;
            padding: 15px;
            border-radius: 4px;
            font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
            height: calc(100vh - 150px); /* Adjust height based on header/footer */
            overflow-y: auto; /* Enable scrolling */
            border: 1px solid #333;
            margin-top: 20px;
        }

        .log-controls {
            margin-bottom: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #fff;
            padding: 10px;
            border-radius: 4px;
            border-bottom: 1px solid #ddd;
        }

        .log-line {
            padding: 2px 0;
            border-bottom: 1px solid #2a2a2a;
            white-space: pre-wrap; /* Preserve formatting */
            font-size: 13px;
            line-height: 1.4;
        }

        /* Log Level Coloring */
        .log-ERROR { color: #ff6b6b; background-color: rgba(255, 107, 107, 0.1); }
        .log-WARN  { color: #feca57; }
        .log-INFO  { color: #54a0ff; }
        .log-DEBUG { color: #c8d6e5; }
        .log-TRACE { color: #576574; }

        .log-header {
            color: #888;
            font-size: 12px;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
<div id="wrapper" class="toggled">
    <%@include file="common/top_menu.jsp" %>
    <%@include file="common/left_menu.jsp" %>

    <div id="page-content-wrapper">
        <%
            // 1. Retrieve the path set by the Controller
            String logFilePath = (String) request.getAttribute("LOG_FILE_LOCATION");

            File logFile = new File(logFilePath);
            List<String> recentLogs = new ArrayList<>();
            String errorMessage = null;
            long totalLines = 0;

            // 2. Read the File (Tail logic - last 1000 lines)
            if (logFile.exists() && logFile.canRead()) {
                try {
                    // Efficiently count lines (can be omitted for speed if needed)
                    // totalLines = Files.lines(logFile.toPath()).count();

                    // Read the last 1000 lines to avoid browser lag
                    long limit = 1000;
                    Path path = logFile.toPath();

                    // Read all lines, skip to the last 'limit', and collect
                    // Note: For very large files (>500MB), consider using RandomAccessFile
                    try (Stream<String> stream = Files.lines(path)) {
                        // We need to know total lines to skip correctly,
                        // but for performance we will just reverse the list after reading all
                        // or use a custom collector. Here is a simple approach:

                        // Read all lines (Warning: memory intensive for huge files)
                        // Optimization: Read specific chunk if possible, but Files.lines is convenient.
                        // Let's stick to reading the last 1000 lines using a buffer approach if possible,
                        // but for standard JSP simplicity, we'll read all and subset.

                        List<String> allLines = Files.readAllLines(path);
                        totalLines = allLines.size();

                        int fromIndex = Math.max(0, allLines.size() - (int)limit);
                        recentLogs = allLines.subList(fromIndex, allLines.size());

                    }
                } catch (IOException e) {
                    errorMessage = "Error reading log file: " + e.getMessage();
                }
            } else {
                errorMessage = "Log file not found or not readable at: " + logFilePath;
            }
        %>

        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-12">
                    <div class="log-controls">
                        <h4 style="margin:0;">Server Logs</h4>
                        <div>
                            <button class="btn btn-sm btn-default" onclick="location.reload();">
                                <span class="glyphicon glyphicon-refresh"></span> Refresh
                            </button>
                        </div>
                    </div>

                    <div class="log-viewer-container" id="logContainer">
                        <% if (errorMessage != null) { %>
                            <div class="alert alert-danger" style="margin: 10px;">
                                <strong>Error:</strong> <%= errorMessage %>
                            </div>
                        <% } else { %>
                            <div class="log-header">
                                Displaying last <%= recentLogs.size() %> entries...
                            </div>
                            <%
                                // Iterate backwards to show newest logs at the top
                                Collections.reverse(recentLogs);
                                for (String line : recentLogs) {
                                    String cssClass = "log-DEBUG";
                                    if (line.contains("ERROR")) cssClass = "log-ERROR";
                                    else if (line.contains("WARN")) cssClass = "log-WARN";
                                    else if (line.contains("INFO")) cssClass = "log-INFO";
                                    else if (line.contains("TRACE")) cssClass = "log-TRACE";
                            %>
                                <div class="log-line <%= cssClass %>"><%= line.replace("<", "<").replace(">", ">") %></div>
                            <%
                                }
                            %>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
<%@include file="common/resoucelink_footer.jsp" %>

<script>
    // Optional: Auto-scroll to top when refreshed
    window.onload = function() {
        var container = document.getElementById("logContainer");
        if(container) {
            container.scrollTop = 0;
        }
    };
</script>

</body>
</html>


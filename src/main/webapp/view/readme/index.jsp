<%-- 
    Document   : db_manager_index
    Created on : Dec 12, 2017, 10:21:54 AM
    Author     : imtiaz
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en" ng-app="SyncApp">
    <head>
        <%@include file="../common/resoucelink_head.jsp" %>
    </head>
    <body>
        <div id="wrapper" class="toggled">
            <%@include file="../common/top_menu.jsp" %>
            <%@include file="../common/left_menu.jsp" %>

            <div id="page-content-wrapper">
                <div class="container-fluid" ng-controller="ReadmeCrtl">

                    <div class="row">
                        <!-- App Config Section -->
                        <%@include file="../readme/app_config.jsp" %>
                    </div>
                    <div class="row">
                        <!-- Server Config Section -->
                        <%@include file="../readme/server_config.jsp" %>
                    </div>

                    <div class="row">
                        <!-- Client Config Section -->
                        <%@include file="../readme/client_config.jsp" %>
                    </div>

                    <div class="row">
                        <!-- Database Config Section -->
                        <%@include file="../readme/database_config.jsp" %>
                    </div>

                    <div class="row">
                        <!-- Table Config Section -->
                        <%@include file="../readme/table_config.jsp" %>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="../common/resoucelink_footer.jsp" %>
    </body>
</html>